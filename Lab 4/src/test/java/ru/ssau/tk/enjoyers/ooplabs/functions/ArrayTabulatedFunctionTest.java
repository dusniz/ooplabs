package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionTest {
    @Test
    void testApply() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(0.0, function.apply(0.0), 1e-12);
        assertEquals(2.5, function.apply(1.5), 1e-12); // интерполяция
        assertEquals(5.5, function.apply(2.5), 1e-12); // экстраполяция
    }

    @Test
    void testConstructor() {
        TabulatedFunction f1 = new ArrayTabulatedFunction(new UnitFunction(), 10, 1, 10);

        assertEquals(1, f1.apply(5), 1e-12);

        TabulatedFunction f2 = new ArrayTabulatedFunction(new ZeroFunction(), 1, 1, 10);

        assertEquals(0, f2.apply(1), 1e-12);
    }

    @Test
    void testSetGet() {
        double[] xValues = {0.0, 1.0, 4.0};
        double[] yValues = {0.0, 1.0, 16.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        double expectedY = 2.0;
        function.setY(2, 2.0);
        assertEquals(expectedY, function.getY(2));
    }

    @Test
    void testIndex() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(1, function.indexOfY(1.0));
        assertEquals(-1, function.indexOfY(144.0));
        assertEquals(3, function.floorIndexOfX(5.0));
        assertEquals(2, function.floorIndexOfX(2.0));
        assertEquals(-1, function.indexOfY(144.0));
    }
}
