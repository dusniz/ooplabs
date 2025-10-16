package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;

public class ReadTask implements Runnable {

    private TabulatedFunction function;

    ReadTask(TabulatedFunction function) { this.function = function; }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            System.out.printf(
                    "After read: i = %d, x = %f, y = %f",
                    i,
                    function.getX(i),
                    function.getY(i)
            );
            System.out.println();
        }
    }
}
