package ru.ssau.tk.enjoyers.ooplabs.operations;

import ru.ssau.tk.enjoyers.ooplabs.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import ru.ssau.tk.enjoyers.ooplabs.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk.enjoyers.ooplabs.functions.factory.ArrayTabulatedFunctionFactory;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {

    private TabulatedFunctionFactory factory;

    public TabulatedDifferentialOperator() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    @Override
    public TabulatedFunction derive(TabulatedFunction function) {
        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        int count = function.getCount();

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        // Копируем x значения
        for (int i = 0; i < count; i++) {
            xValues[i] = points[i].x;
        }

        // Вычисляем производные для первых n-1 точек (правая разностная производная)
        for (int i = 0; i < count - 1; i++) {
            yValues[i] = (points[i + 1].y - points[i].y) / (points[i + 1].x - points[i].x);
        }

        // Последняя точка вычисляется по левой разностной производной
        yValues[count - 1] = yValues[count - 2];

        return factory.create(xValues, yValues);
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
        if (function instanceof SynchronizedTabulatedFunction){
            SynchronizedTabulatedFunction syncFunction = (SynchronizedTabulatedFunction) (function);
            return syncFunction.doSynchronously(this::derive);
        }
        else {
            SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(function);
            return syncFunction.doSynchronously(this::derive);
        }
    }
}