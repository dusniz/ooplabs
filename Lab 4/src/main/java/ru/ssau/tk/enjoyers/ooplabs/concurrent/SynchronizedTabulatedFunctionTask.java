package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import java.util.concurrent.locks.*;

public class SynchronizedTabulatedFunctionTask implements Runnable {
    private boolean completed = false;
    private static final Lock lock = new ReentrantLock();
    private final SynchronizedTabulatedFunction function;

    public SynchronizedTabulatedFunctionTask(SynchronizedTabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            for (int i = 0; i < function.getCount(); i++) {
                function.setY(i, function.getY(i) * 2);
            }
        }
        finally {
            lock.unlock();
            completed = true;
            System.out.println(Thread.currentThread().getName() + " закончил выполнение задачи");
        }
    }

    public boolean isCompleted() {
        return completed;
    }
}
