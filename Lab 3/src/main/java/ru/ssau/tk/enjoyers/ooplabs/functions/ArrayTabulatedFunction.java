package ru.ssau.tk.enjoyers.ooplabs.functions;

import java.util.Arrays;
import java.util.Iterator;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable {
    private double[] xValues;
    private double[] yValues;
    private int count;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {

        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom,
                                  double xTo, int count) {
        if (count < 2) throw new IllegalArgumentException("Count must be >= 2");

        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        this.count = count;
        this.xValues = new double[count];
        this.yValues = new double[count];

        if (xFrom == xTo) {
            Arrays.fill(xValues, xFrom);
            double y = source.apply(xFrom);
            Arrays.fill(yValues, y);
        } else {
            double step = (xTo - xFrom) / (count - 1);
            for (int i = 0; i < count; i++) {
                xValues[i] = xFrom + i * step;
                yValues[i] = source.apply(xValues[i]);
            }
        }
    }

    @Override
    public int getCount() { return count; }

    @Override
    public double getX(int index) { return xValues[index]; }

    @Override
    public double getY(int index) { return yValues[index]; }

    @Override
    public void setY(int index, double value) { yValues[index] = value; }

    @Override
    public int indexOfX(double x) {
        for (int i = 0; i < count; i++) {
            if (Math.abs(xValues[i] - x) < 1e-12) return i;
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        for (int i = 0; i < count; i++) {
            if (Math.abs(yValues[i] - y) < 1e-12) return i;
        }
        return -1;
    }

    @Override
    public double leftBound() { return xValues[0]; }

    @Override
    public double rightBound() { return xValues[count - 1]; }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < xValues[0]) return 0;
        if (x > xValues[count - 1]) return count;

        for (int i = 1; i < count; i++) {
            if (x < xValues[i]) return i - 1;
        }
        return count - 1;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return interpolate(x, 0);
    }

    @Override
    protected double extrapolateRight(double x) {
        return interpolate(x, count - 2);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (count == 1) return yValues[0];

        double leftX = xValues[floorIndex];
        double rightX = xValues[floorIndex + 1];
        double leftY = yValues[floorIndex];
        double rightY = yValues[floorIndex + 1];

        return interpolate(x, leftX, rightX, leftY, rightY);
    }

    @Override
    public void insert(double x, double y) {
        int index = indexOfX(x);
        if (index != -1) {
            setY(index, y);
            return;
        }

        // Увеличиваем массивы
        double[] newX = new double[count + 1];
        double[] newY = new double[count + 1];

        int i = 0;
        // Ищем место для вставки
        while (i < count && xValues[i] < x) {
            i++;
        }

        System.arraycopy(xValues, 0, newX, 0, i);
        newX[i] = x;
        System.arraycopy(xValues, i, newX, i + 1, count - i);

        System.arraycopy(yValues, 0, newY, 0, i);
        newY[i] = y;
        System.arraycopy(yValues, i, newY, i + 1, count - i);

        xValues = newX;
        yValues = newY;
        count++;
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        double[] newX = new double[count - 1];
        double[] newY = new double[count - 1];

        // Копируем элементы до удаляемого
        System.arraycopy(xValues, 0, newX, 0, index);
        System.arraycopy(yValues, 0, newY, 0, index);

        // Копируем элементы после удаляемого
        System.arraycopy(xValues, index + 1, newX, index, count - index - 1);
        System.arraycopy(yValues, index + 1, newY, index, count - index - 1);

        xValues = newX;
        yValues = newY;
        count--;
    }

    @Override
    public Iterator<Point> iterator() {
        throw new UnsupportedOperationException();
    }
}