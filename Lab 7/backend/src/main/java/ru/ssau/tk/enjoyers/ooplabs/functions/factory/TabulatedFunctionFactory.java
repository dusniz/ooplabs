package ru.ssau.tk.enjoyers.ooplabs.functions.factory;

import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.StrictTabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.UnmodifiableTabulatedFunction;

public interface TabulatedFunctionFactory {

    TabulatedFunction create(double[] xValues, double[] yValues);

    default TabulatedFunction createStrict(double[] xValues, double[] yValues) {
        return new StrictTabulatedFunction(create(xValues, yValues));
    }

    default TabulatedFunction createUnmodifiable(double[] xValues, double[] yValues) {
        return new UnmodifiableTabulatedFunction(create(xValues, yValues));
    }

    default TabulatedFunction createStrictUnmodifiable(double[] xValues, double[] yValues) {
        TabulatedFunction function = create(xValues, yValues);
        return new UnmodifiableTabulatedFunction(new StrictTabulatedFunction(function));
    }
}