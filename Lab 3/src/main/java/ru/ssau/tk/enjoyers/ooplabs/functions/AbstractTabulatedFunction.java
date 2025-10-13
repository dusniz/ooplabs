package ru.ssau.tk.enjoyers.ooplabs.functions;

import ru.ssau.tk.enjoyers.ooplabs.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk.enjoyers.ooplabs.exceptions.DifferentLengthOfArraysException;
import ru.ssau.tk.enjoyers.ooplabs.exceptions.InterpolationException;

public abstract class AbstractTabulatedFunction implements TabulatedFunction {

    protected abstract int floorIndexOfX(double x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x, int floorIndex);

    protected double interpolate(double x, double leftX, double rightX,
                                 double leftY, double rightY) {
        if (x <= leftX || x >= rightX) {
            throw new InterpolationException("x is outside interpolation interval");
        }
        return leftY + (rightY - leftY) * (x - leftX) / (rightX - leftX);
    }

    @Override
    public double apply(double x) {
        if (x < leftBound()) {
            return extrapolateLeft(x);
        } else if (x > rightBound()) {
            return extrapolateRight(x);
        } else {
            int index = indexOfX(x);
            if (index != -1) {
                return getY(index);
            } else {
                int floorIndex = floorIndexOfX(x);
                return interpolate(x, floorIndex);
            }
        }
    }

    public static void checkLengthIsTheSame(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            throw new DifferentLengthOfArraysException("Length is not the same");
        }
    }

    public static void checkSorted(double[] xValues) {
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new ArrayIsNotSortedException("Array is not sorted");
            }
        }
    }
}