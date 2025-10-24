package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable {
    private static final Logger logger = LogManager.getLogger(MultiplyingTask.class);
    private boolean completed = false;
    private final TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        logger.info("Создание MultiplyingTask");
        this.function = function;
        logger.info("Создано MultiplyingTask");
    }

    @Override
    public void run() {
        logger.info("Запущено MultiplyingTask");
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                function.setY(i, function.getY(i) * 2);
            }
        }

        completed = true;
        logger.info("Выполнено MultiplyingTask");
    }

    public boolean isCompleted() {
        return completed;
    }
}
