package ru.ssau.tk.enjoyers.ooplabs.operations;

import ru.ssau.tk.enjoyers.ooplabs.functions.MathFunction;

public interface DifferentialOperator<T extends MathFunction> {
    T derive(T function);
}