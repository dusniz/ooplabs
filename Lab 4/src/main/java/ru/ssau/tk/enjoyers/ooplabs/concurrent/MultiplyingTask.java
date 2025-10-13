package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable{
    private final TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            function.setY(i, function.getY(i)*2);
        }
        System.out.println(Thread.currentThread().getName() + "закончил выполнение задачи");
    }
}
