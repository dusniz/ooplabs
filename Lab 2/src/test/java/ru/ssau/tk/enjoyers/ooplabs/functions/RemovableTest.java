package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemovableTest {

    private LinkedListTabulatedFunction linkedListFunc;
    private LinkedListTabulatedFunction emptyList;
    private LinkedListTabulatedFunction singlePointList;

    @Test
    void testRemoveFirst() {
        // Основная функция с несколькими точками
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
        linkedListFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // Пустой список
        emptyList = new LinkedListTabulatedFunction(new double[0], new double[0]);

        // Список с одной точкой
        double[] singleX = {5.0};
        double[] singleY = {25.0};
        singlePointList = new LinkedListTabulatedFunction(singleX, singleY);

        // Удалить первый
        linkedListFunc.remove(0);
        assertEquals(4, linkedListFunc.getCount());
        assertEquals(1.0, linkedListFunc.getX(0), 1e-12);
        assertEquals(2.0, linkedListFunc.getX(1), 1e-12);
        assertEquals(4.0, linkedListFunc.getX(3), 1e-12);

        // Удалить последний
        linkedListFunc.remove(4);
        assertEquals(4, linkedListFunc.getCount());
        assertEquals(0.0, linkedListFunc.getX(0), 1e-12);
        assertEquals(1.0, linkedListFunc.getX(1), 1e-12);
        assertEquals(3.0, linkedListFunc.getX(3), 1e-12);

        // Удалить средний
        linkedListFunc.remove(2);
        assertEquals(4, linkedListFunc.getCount());
        assertEquals(0.0, linkedListFunc.getX(0), 1e-12);
        assertEquals(1.0, linkedListFunc.getX(1), 1e-12);
        assertEquals(3.0, linkedListFunc.getX(2), 1e-12);
        assertEquals(4.0, linkedListFunc.getX(3), 1e-12);
        assertEquals(9.0, linkedListFunc.getY(2), 1e-12);

        // Удалить 1 элемент
        singlePointList.remove(0);
        assertEquals(0, singlePointList.getCount());
    }

    @Test
    void testRemoveInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> linkedListFunc.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> linkedListFunc.remove(5));
        assertThrows(IndexOutOfBoundsException.class, () -> emptyList.remove(0));
    }
}
