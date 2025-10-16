package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;

public class WriteTask implements Runnable {

    private TabulatedFunction function;
    private double value;

    WriteTask(TabulatedFunction function, double value) {
        this.function = function;
        this.value = value;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            function.setY(i, value);
            System.out.printf("Writing for index %d complete", i);
        }
    }
}