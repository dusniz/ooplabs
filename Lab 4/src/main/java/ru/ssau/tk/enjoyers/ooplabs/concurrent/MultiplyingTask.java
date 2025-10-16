package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable {
    private boolean completed = false;
    private final TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                function.setY(i, function.getY(i) * 2);
            }
        }
        completed = true;
        System.out.println(Thread.currentThread().getName() + " закончил выполнение задачи");
    }

    public boolean isCompleted() {
        return completed;
    }
}
