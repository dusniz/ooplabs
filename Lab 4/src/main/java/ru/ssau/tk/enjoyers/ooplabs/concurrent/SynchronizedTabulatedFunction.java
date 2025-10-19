package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import ru.ssau.tk.enjoyers.ooplabs.functions.Point;
import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;
import java.util.Iterator;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction function;
    public final Object lock = new Object();

    public SynchronizedTabulatedFunction(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public double apply(double x)
    {
        synchronized (lock) {
            return function.apply(x);
        }
    }

    @Override
    public int getCount() {
        synchronized (lock) {
            return function.getCount();
        }
    }

    @Override
    public double getX(int index) {
        synchronized (lock) {
            return function.getX(index);
        }
    }

    @Override
    public double getY(int index) {
        synchronized (lock) {
            return function.getY(index);
        }
    }

    @Override
    public void setY(int index, double value) {
        synchronized (lock) {
            function.setY(index, value);
        }
    }

    @Override
    public int indexOfX(double x) {
        synchronized (lock) {
            return function.indexOfX(x);
        }
    }

    @Override
    public int indexOfY(double y) {
        synchronized (lock) {
            return function.indexOfY(y);
        }
    }

    @Override
    public double leftBound() {
        synchronized (lock) {
            return function.leftBound();
        }
    }

    @Override
    public double rightBound() {
        synchronized (lock) {
            return function.rightBound();
        }
    }

    public Iterator<Point> iterator() {
        synchronized (lock) {
            return function.iterator();
        }
    }
}
