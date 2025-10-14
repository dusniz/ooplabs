package ru.ssau.tk.enjoyers.ooplabs.functions.factory;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.enjoyers.ooplabs.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class FactoryTest {

    @Test
    void testArrayTabulatedFunctionFactory() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        TabulatedFunction function = factory.create(xValues, yValues);
        assertInstanceOf(ArrayTabulatedFunction.class, function);
        assertEquals(3, function.getCount());
        assertEquals(4.0, function.apply(2.0), 1e-12);
    }

    @Test
    void testLinkedListTabulatedFunctionFactory() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        TabulatedFunction function = factory.create(xValues, yValues);
        assertInstanceOf(LinkedListTabulatedFunction.class, function);
        assertEquals(3, function.getCount());
        assertEquals(4.0, function.apply(2.0), 1e-12);
    }

    @Test
    void testCreateStrict() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        TabulatedFunction strictFunction = factory.createStrict(xValues, yValues);
        assertInstanceOf(StrictTabulatedFunction.class, strictFunction);

        // Should work for exact points
        assertEquals(1.0, strictFunction.apply(1.0), 1e-12);

        // Should throw for interpolation
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(0.5));
    }

    @Test
    void testCreateUnmodifiable() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        TabulatedFunction unmodifiableFunction = factory.createUnmodifiable(xValues, yValues);
        assertInstanceOf(UnmodifiableTabulatedFunction.class, unmodifiableFunction);

        // Should allow reading
        assertEquals(1.0, unmodifiableFunction.getY(1), 1e-12);

        // Should throw for modification
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableFunction.setY(1, 10.0));
    }

    @Test
    void testCreateStrictUnmodifiable() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        TabulatedFunction doubleWrapped = factory.createStrictUnmodifiable(xValues, yValues);

        // Should have both properties
        assertEquals(1.0, doubleWrapped.apply(1.0), 1e-12);
        assertThrows(UnsupportedOperationException.class, () -> doubleWrapped.apply(0.5)); // from Strict
        assertThrows(UnsupportedOperationException.class, () -> doubleWrapped.setY(1, 10.0)); // from Unmodifiable
    }
}