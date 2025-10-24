package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemovableTest {

    private LinkedListTabulatedFunction linkedListFunc;

    @Test
    public void testListRemove() {
        // Основная функция с несколькими точками
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
        linkedListFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // Удалить первый
        linkedListFunc.remove(0);
        assertEquals(4, linkedListFunc.getCount());
        assertEquals(1.0, linkedListFunc.getX(0), 1e-12);
        assertEquals(2.0, linkedListFunc.getX(1), 1e-12);
        assertEquals(4.0, linkedListFunc.getX(3), 1e-12);

        // Удалить последний
        linkedListFunc.remove(3);
        assertEquals(3, linkedListFunc.getCount());
        assertEquals(1.0, linkedListFunc.getX(0), 1e-12);
        assertEquals(3.0, linkedListFunc.getX(2), 1e-12);

        // Удалить средний
        linkedListFunc.remove(1);
        assertEquals(2, linkedListFunc.getCount());
        assertEquals(1.0, linkedListFunc.getX(0), 1e-12);
        assertEquals(3.0, linkedListFunc.getX(1), 1e-12);
        assertEquals(9.0, linkedListFunc.getY(1), 1e-12);
    }

    @Test
    public void testArrayRemove() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);
        assertEquals(3, function.getCount());
        assertEquals(0.0, function.getX(0), 1e-12);
        assertEquals(2.0, function.getX(1), 1e-12);
        assertEquals(3.0, function.getX(2), 1e-12);

        function.remove(0);
        assertEquals(2, function.getCount());
        assertEquals(2.0, function.getX(0), 1e-12);

        function.remove(1);
        assertEquals(1, function.getCount());
        assertEquals(2.0, function.getX(0), 1e-12);
    }
}

