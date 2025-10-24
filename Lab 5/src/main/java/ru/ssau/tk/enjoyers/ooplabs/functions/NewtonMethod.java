package ru.ssau.tk.enjoyers.ooplabs.functions;

public class NewtonMethod implements MathFunction {
    private final MathFunction function;    // Функция, корень которой ищем
    private final MathFunction derivative;  // Производная фукнции, корень которой ищем
    private final double tolerance;         // Погрешность поиска корня
    private final int maxIterations;        // Максимальное кол-во итераций метода

    public NewtonMethod(MathFunction targetFunction, MathFunction derivative,
                        double tolerance, int maxIterations) {
        this.function = targetFunction;
        this.derivative = derivative;
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }

    @Override
    public double apply(double initialX) {
        // Возвращает найденный корень
        double x = initialX;

        for (int i = 0; i < maxIterations; i++) {
            double fx = function.apply(x);
            double dfx = derivative.apply(x);

            if (Math.abs(fx) < tolerance) {
                return x;  // Корень найден с учётом погрешности
            }

            if (Math.abs(dfx) < 1e-12) {
                throw new ArithmeticException("Derivative too small at x = " + x);
            }

            x = x - fx / dfx;  // Один шаг метода Ньютона
        }

        return x;  // Возвращаем последнее (обычно лучшее) найденное приближение
    }
}
