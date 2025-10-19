package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkedListTabulatedFunctionTest {

    @Test
    public void testApply() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0.0, function.apply(0.0), 1e-12);
        assertEquals(2.5, function.apply(1.5), 1e-12); // интерполяция
        assertEquals(5.5, function.apply(2.5), 1e-12); // экстраполяция
    }

    @Test
    public void testConstructor() {
        TabulatedFunction f1 = new LinkedListTabulatedFunction(new UnitFunction(), 10, 1, 10);

        assertEquals(1, f1.apply(5), 1e-12);

        TabulatedFunction f2 = new LinkedListTabulatedFunction(new ZeroFunction(), 1, 1, 10);

        assertEquals(0, f2.apply(1), 1e-12);
    }

    @Test
    public void testSetGet() {
        double[] xValues = {0.0, 1.0, 4.0};
        double[] yValues = {0.0, 1.0, 16.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        double expectedY = 2.0;
        function.setY(2, 2.0);
        assertEquals(expectedY, function.getY(2));
    }

    @Test
    public void testIndex() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(1, function.indexOfY(1.0));
        assertEquals(-1, function.indexOfY(144.0));
        assertEquals(3, function.floorIndexOfX(5.0));
        assertEquals(0, function.floorIndexOfX(0.0));
        assertEquals(1, function.floorIndexOfX(1.0));
        assertEquals(3, function.floorIndexOfX(2.0));
        assertEquals(-1, function.indexOfY(144.0));
    }
}
