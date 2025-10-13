package ru.ssau.tk.enjoyers.ooplabs.operations;

import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import ru.ssau.tk.enjoyers.ooplabs.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk.enjoyers.ooplabs.functions.factory.ArrayTabulatedFunctionFactory;

public class TabulatedFunctionOperationService {
    private TabulatedFunctionFactory factory;

    public TabulatedFunctionOperationService() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public static Point[] asPoints(TabulatedFunction function) {
        Point[] points = new Point[function.getCount()];
        int i = 0;
        for (Point point : function) {
            points[i++] = point;
        }
        return points;
    }
}
