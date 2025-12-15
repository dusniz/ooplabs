package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;

public class ReadTask implements Runnable {
    private static final Logger logger = LogManager.getLogger(MultiplyingTask.class);
    private TabulatedFunction function;

    ReadTask(TabulatedFunction function) {
        logger.info("Создание ReadTask");
        this.function = function;
        logger.info("Создано ReadTask");
    }

    @Override
    public void run() {
        logger.info("Запущено ReadTask");
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                System.out.printf(
                        "After read: i = %d, x = %f, y = %f",
                        i,
                        function.getX(i),
                        function.getY(i)
                );
                System.out.println();
            }
        }
        logger.info("Выполнено ReadTask");
    }
}
