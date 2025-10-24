package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;

public class WriteTask implements Runnable {
    private static final Logger logger = LogManager.getLogger(MultiplyingTask.class);
    private TabulatedFunction function;
    private double value;

    WriteTask(TabulatedFunction function, double value) {
        logger.info("Создание WriteTask");
        this.function = function;
        this.value = value;
        logger.info("Создано WriteTask");
    }

    @Override
    public void run() {
        logger.info("Запущено WriteTask");
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                function.setY(i, value);
                System.out.printf("Writing for index %d complete", i);
                System.out.println();
            }
        }
        logger.info("Выполнено WriteTask");
    }
}