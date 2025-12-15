package ru.ssau.tk.enjoyers.ooplabs.functions;

public class DeBoorFunction implements MathFunction {
    private final double[] knots;
    private final double[] controlPoints;
    private final int degree;

    public DeBoorFunction(double[] knots, double[] controlPoints, int degree) {
        this.knots = knots.clone();
        this.controlPoints = controlPoints.clone();
        this.degree = degree;
    }

    @Override
    public double apply(double x) {
        return deBoor(degree, knots, controlPoints, x);
    }

    private double deBoor(int k, double[] t, double[] c, double x) {
        // Упрощённая реализация алгоритма де Бура
        for (int r = 1; r <= k; r++) {
            for (int j = k; j >= r; j--) {
                double alpha = (x - t[j]) / (t[j + k - r + 1] - t[j]);
                c[j] = (1 - alpha) * c[j - 1] + alpha * c[j];
            }
        }
        return c[k];
    }
}