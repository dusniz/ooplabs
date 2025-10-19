package ru.ssau.tk.enjoyers.ooplabs.functions;

import java.util.Iterator;

public class MockTabulatedFunction extends AbstractTabulatedFunction {
    private final double x0 = 0.0;
    private final double x1 = 1.0;
    private final double y0 = 0.0;
    private final double y1 = 2.0;

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public double getX(int index) {
        return index == 0 ? x0 : x1;
    }

    @Override
    public double getY(int index) {
        return index == 0 ? y0 : y1;
    }

    @Override
    public void setY(int index, double value) {
        throw new UnsupportedOperationException("Called setY on mock object");
    }

    @Override
    public int indexOfX(double x) {
        return Math.abs(x - x0) < 1e-12 ? 0 :
                Math.abs(x - x1) < 1e-12 ? 1 : -1;
    }

    @Override
    public int indexOfY(double y) {
        return Math.abs(y - y0) < 1e-12 ? 0 :
                Math.abs(y - y1) < 1e-12 ? 1 : -1;
    }

    @Override
    public double leftBound() {
        return x0;
    }

    @Override
    public double rightBound() {
        return x1;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < x0) return 0;
        if (x > x1) return 2;
        return x < (x0 + x1) / 2 ? 0 : 1;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return y0 + (y1 - y0) / (x1 - x0) * (x - x0);
    }

    @Override
    protected double extrapolateRight(double x) {
        return y1 + (y1 - y0) / (x1 - x0) * (x - x1);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        return interpolate(x, x0, x1, y0, y1);
    }

    @Override
    public Iterator<Point> iterator(){
        throw new UnsupportedOperationException();
    }
}
