package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.enjoyers.ooplabs.functions.factory.*;
import ru.ssau.tk.enjoyers.ooplabs.operations.LeftSteppingDifferentialOperator;
import ru.ssau.tk.enjoyers.ooplabs.operations.MiddleSteppingDifferentialOperator;
import ru.ssau.tk.enjoyers.ooplabs.operations.RightSteppingDifferentialOperator;
import ru.ssau.tk.enjoyers.ooplabs.operations.TabulatedDifferentialOperator;

import static org.junit.jupiter.api.Assertions.*;

class DifferentialOperatorsTest {

    @Test
    void testTabulatedDifferentialOperator() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {0.0, 0.1, 0.2, 0.3};
        double[] yValues = {0.0, 0.01, 0.04, 0.09}; // x^2
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.derive(function);

        assertEquals(4, derivative.getCount());
        assertEquals(0.0, derivative.getY(0), 0.2); // x=0: 2*0=0
        assertEquals(0.2, derivative.getY(1), 0.2); // x=1: 2*1=2
        assertEquals(0.4, derivative.getY(2), 0.2); // x=2: 2*2=4
        assertEquals(0.6, derivative.getY(3), 0.2);
    }

    @Test
    void testLeftSteppingDifferentialOperator() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.001);
        SqrFunction sqr = new SqrFunction();

        MathFunction derivative = operator.derive(sqr);

        // For f(x)=x^2, f'(x)=2x
        assertEquals(0.0, derivative.apply(0.0), 0.1);
        assertEquals(2.0, derivative.apply(1.0), 0.1);
        assertEquals(4.0, derivative.apply(2.0), 0.1);
    }

    @Test
    void testRightSteppingDifferentialOperator() {
        RightSteppingDifferentialOperator operator = new RightSteppingDifferentialOperator(0.001);
        SqrFunction sqr = new SqrFunction();

        MathFunction derivative = operator.derive(sqr);

        // For f(x)=x^2, f'(x)=2x
        assertEquals(0.0, derivative.apply(0.0), 0.1);
        assertEquals(2.0, derivative.apply(1.0), 0.1);
        assertEquals(4.0, derivative.apply(2.0), 0.1);
    }

    @Test
    void testMiddleSteppingDifferentialOperator() {
        MiddleSteppingDifferentialOperator operator = new MiddleSteppingDifferentialOperator(0.001);
        SqrFunction sqr = new SqrFunction();

        MathFunction derivative = operator.derive(sqr);

        assertEquals(0.0, derivative.apply(0.0), 0.1);
        assertEquals(2.0, derivative.apply(1.0), 0.1);
        assertEquals(4.0, derivative.apply(2.0), 0.1);
    }

    @Test
    void testInvalidStep() {
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(0));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(-1));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(Double.POSITIVE_INFINITY));
    }

    @Test
    void testDifferentFactories() {
        TabulatedDifferentialOperator arrayOperator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = arrayOperator.derive(function);
        assertTrue(derivative instanceof ArrayTabulatedFunction);

        TabulatedDifferentialOperator linkedOperator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());
        derivative = linkedOperator.derive(function);
        assertTrue(derivative instanceof LinkedListTabulatedFunction);
    }
}