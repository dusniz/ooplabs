package ru.ssau.tk.enjoyers.ooplabs.concurrent;

public class SynchronizedTabulatedFunctionTask implements Runnable {
    private boolean completed = false;
    private final SynchronizedTabulatedFunction function;

    public SynchronizedTabulatedFunctionTask(SynchronizedTabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        synchronized (function.lock) {
            for (int i = 0; i < function.getCount(); i++) {
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
