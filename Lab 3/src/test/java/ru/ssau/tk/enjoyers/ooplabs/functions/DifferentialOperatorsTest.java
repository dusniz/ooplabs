package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.enjoyers.ooplabs.functions.factory.*;
import ru.ssau.tk.enjoyers.ooplabs.operations.*;

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
        // For x^2, derivative should be 2x
        assertEquals(0.0, derivative.getY(0), 0.2); // at x=0: 2*0=0
        assertEquals(0.2, derivative.getY(1), 0.2); // at x=1: 2*1=2
        assertEquals(0.4, derivative.getY(2), 0.2); // at x=2: 2*2=4
        assertEquals(0.6, derivative.getY(3), 0.2); // last point same as previous
    }

    @Test
    void testDifferentFactories() {
        // Test with array factory
        TabulatedDifferentialOperator arrayOperator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = arrayOperator.derive(function);
        assertTrue(derivative instanceof ArrayTabulatedFunction);

        // Test with linked list factory
        TabulatedDifferentialOperator linkedOperator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());
        derivative = linkedOperator.derive(function);
        assertTrue(derivative instanceof LinkedListTabulatedFunction);
    }
}