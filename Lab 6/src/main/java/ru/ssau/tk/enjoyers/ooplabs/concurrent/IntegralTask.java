package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import java.util.concurrent.Callable;

public class IntegralTask implements Callable<Double> {
    private static final Logger logger = LogManager.getLogger(IntegralTask.class);
    private final TabulatedFunction function;
    private final double startX;     // Начало области интегрирования
    private final double endX;       // Конец области интегрирования

    public IntegralTask(TabulatedFunction function, double startX, double endX) {
        logger.info("Создание IntegralTask");
        this.function = function;
        this.startX = startX;
        this.endX = endX;
        logger.info("IntegralTask успешно создано");
    }

    @Override
    public Double call() {
        logger.info("Запущено IntegralTask");
        // Находим, какие точки функции попадают в наш отрезок
        int startIndex = findNearestIndex(startX);
        int endIndex = findNearestIndex(endX);

        double sum = 0.0;
        // Интегрируем методом трапеций на найденном участке
        for (int i = startIndex; i < endIndex; i++) {
            double x1 = function.getX(i);
            double x2 = function.getX(i + 1);
            double y1 = function.getY(i);
            double y2 = function.getY(i + 1);

            // Площадь трапеции
            double area = (y1 + y2) * (x2 - x1) / 2.0;
            sum += area;
        }
        logger.info("Выполнено IntegralTask");

        return sum;
    }

    private int findNearestIndex(double x) {
        // Находим индекс ближайшей точки к x
        for (int i = 0; i < function.getCount() - 1; i++) {
            if (function.getX(i) <= x && function.getX(i + 1) >= x) {
                return i;
            }
        }
        return function.getCount() - 1;
    }
}

