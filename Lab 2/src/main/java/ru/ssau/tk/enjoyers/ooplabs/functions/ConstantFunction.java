package ru.ssau.tk.enjoyers.ooplabs.functions;

public class ConstantFunction implements MathFunction {

    private final double x;

    ConstantFunction(double x){
        this.x = x;
    }

    @Override
    public double apply(double x) {
        return this.x;
    }
}
