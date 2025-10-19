package ru.ssau.tk.enjoyers.ooplabs.operations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.ssau.tk.enjoyers.ooplabs.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.factory.*;
import ru.ssau.tk.enjoyers.ooplabs.functions.*;

class DifferentialOperatorsTest {

    @Test
    public void testTabulatedDifferentialOperator() {
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
    public void testLeftSteppingDifferentialOperator() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(1);
        operator.setStep(0.001);
        assertEquals(0.001, operator.getStep(), 1e-12);

        SqrFunction sqr = new SqrFunction();

        MathFunction derivative = operator.derive(sqr);

        // f(x)=x^2, f'(x)=2x
        assertEquals(0.0, derivative.apply(0.0), 0.1);
        assertEquals(2.0, derivative.apply(1.0), 0.1);
        assertEquals(4.0, derivative.apply(2.0), 0.1);
    }

    @Test
    public void testRightSteppingDifferentialOperator() {
        RightSteppingDifferentialOperator operator = new RightSteppingDifferentialOperator(0.001);
        SqrFunction sqr = new SqrFunction();

        MathFunction derivative = operator.derive(sqr);

        // f(x)=x^2, f'(x)=2x
        assertEquals(0.0, derivative.apply(0.0), 0.1);
        assertEquals(2.0, derivative.apply(1.0), 0.1);
        assertEquals(4.0, derivative.apply(2.0), 0.1);
    }

    @Test
    public void testMiddleSteppingDifferentialOperator() {
        MiddleSteppingDifferentialOperator operator = new MiddleSteppingDifferentialOperator(0.001);
        SqrFunction sqr = new SqrFunction();

        MathFunction derivative = operator.derive(sqr);

        assertEquals(0.0, derivative.apply(0.0), 0.1);
        assertEquals(2.0, derivative.apply(1.0), 0.1);
        assertEquals(4.0, derivative.apply(2.0), 0.1);
    }

    @Test
    public void testDifferentFactories() {
        TabulatedDifferentialOperator arrayOperator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = arrayOperator.derive(function);
        assertInstanceOf(ArrayTabulatedFunction.class, derivative);

        TabulatedDifferentialOperator linkedOperator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());
        derivative = linkedOperator.derive(function);
        assertInstanceOf(LinkedListTabulatedFunction.class, derivative);

        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        operator.setFactory(new ArrayTabulatedFunctionFactory());
        assertInstanceOf(TabulatedFunctionFactory.class, arrayOperator.getFactory());
    }

    @Test
    void testDeriveSynchronouslyWithRegularFunction() {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        assertEquals(10, derivative.getCount());
        assertEquals(0.0, derivative.getY(0), 1e-9); // Производная константы = 0
    }

    @Test
    void testDeriveSynchronouslyWithSynchronizedFunction() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.deriveSynchronously(syncFunction);

        assertEquals(10, derivative.getCount());
        assertEquals(0.0, derivative.getY(0), 1e-9);
    }

    @Test
    void testDeriveSynchronouslyWithLinearFunction() {
        // f(x) = 2x + 1, производная = 2
        double[] xValues = {0, 1, 2, 3, 4};
        double[] yValues = {1, 3, 5, 7, 9};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        for (int i = 0; i < derivative.getCount() - 1; i++) {
            assertEquals(2.0, derivative.getY(i), 1e-9);
        }
    }
}