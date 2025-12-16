package ru.ssau.tk.enjoyers.ooplabs.functions;

public class CotangentFunction implements MathFunction {

    @Override
    public double apply(double x) { return 1/Math.tan(x); }
}
