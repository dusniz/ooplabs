package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IntegralCalculator {
    private static final Logger logger = LogManager.getLogger(IntegralCalculator.class);
    private final TabulatedFunction function;
    private final int numThreads;

    public IntegralCalculator(TabulatedFunction function, int numThreads) {
        this.function = function;
        this.numThreads = numThreads;
    }

    public double calculate() throws Exception {
        logger.info("Начало работы IntegralCalculator");
        double a = function.leftBound();  // Начало области определения
        double b = function.rightBound(); // Конец области определения
        double segmentLength = (b - a) / numThreads;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Double>> futures = new ArrayList<>();

        // Разбиваем область определения на равные отрезки
        for (int i = 0; i < numThreads; i++) {
            double segmentStart = a + i * segmentLength;
            double segmentEnd = (i == numThreads - 1) ? b : a + (i + 1) * segmentLength;

            IntegralTask task = new IntegralTask(function, segmentStart, segmentEnd);
            futures.add(executor.submit(task));
        }

        // Суммируем результаты
        double total = 0.0;
        for (Future<Double> future : futures) {
            total += future.get();
        }

        executor.shutdown();
        logger.info("Конец работы IntegralCalculator");
        return total;
    }
}
