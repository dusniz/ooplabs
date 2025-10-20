package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import java.util.concurrent.Callable;

public class IntegralTask implements Callable<Double> {
    private final TabulatedFunction function;
    private final double startX;     // Начало области интегрирования
    private final double endX;       // Конец области интегрирования

    public IntegralTask(TabulatedFunction function, double startX, double endX) {
        this.function = function;
        this.startX = startX;
        this.endX = endX;
    }

    @Override
    public Double call() {
        // Находим, какие точки функции попадают в наш отрезок
        int startIndex = findNearestIndex(startX);
        int endIndex = findNearestIndex(endX);

        double sum = 0.0;

        // Интегрируем методом трапеций на найденном участке
        for (int i = startIndex; i < endIndex; i++) {
            double x1 = function.getX(i);
            double x2 = function.getX(i + 1);
            double y1 = function.getY(i);
            double y2 = function.getY(i + 1);

            // Площадь трапеции
            double area = (y1 + y2) * (x2 - x1) / 2.0;
            sum += area;
        }
        System.out.println(Thread.currentThread().getName() + " закончил выполнение задачи IntegralTask");

        return sum;
    }

    private int findNearestIndex(double x) {
        // Находим индекс ближайшей точки к x
        for (int i = 0; i < function.getCount() - 1; i++) {
            if (function.getX(i) <= x && function.getX(i + 1) >= x) {
                return i;
            }
        }
        return function.getCount() - 1;
    }
}

