package ru.ssau.tk.enjoyers.ooplabs.functions.factory;

import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.ArrayTabulatedFunction;

public class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {

    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        return new ArrayTabulatedFunction(xValues, yValues);
    }
}