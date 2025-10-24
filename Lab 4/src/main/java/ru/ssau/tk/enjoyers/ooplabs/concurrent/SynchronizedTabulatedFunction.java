package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import ru.ssau.tk.enjoyers.ooplabs.functions.Point;
import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.operations.TabulatedFunctionOperationService;

import java.util.Iterator;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction function;
    private final Object lock = new Object();

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
        Point[] points;
        synchronized (lock) {
            points = TabulatedFunctionOperationService.asPoints(function);
        }

        return new Iterator<Point>() {
            private int currentIndex = 0;
            private final Point[] pointsCopy = points;

            @Override
            public boolean hasNext() {
                return currentIndex < pointsCopy.length;
            }

            @Override
            public Point next() {
                return pointsCopy[currentIndex++];
            }
        };
    }

    public interface Operation<T> {
        T apply(TabulatedFunction function);
    }

    public <T> T doSynchronously(Operation<T> operation) {
        synchronized (lock) {
            return operation.apply(this);
        }
    }

    public Object getLock(){
        return lock;
    }
}
