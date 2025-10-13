package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InsertableTest {

    @Test
    void testArrayInsert() {
        double[] xValues = {0.0, 2.0};
        double[] yValues = {0.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Вставка в середину
        function.insert(1.0, 1.0);
        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(1), 1e-12);
        assertEquals(1.0, function.getY(1), 1e-12);

        // Вставка существующего x (должен обновиться y)
        function.insert(1.0, 2.0);
        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getY(1), 1e-12);

        // Вставка в начало
        function.insert(-1.0, -1.0);
        assertEquals(4, function.getCount());
        assertEquals(-1.0, function.getX(0), 1e-12);

        // Вставка в конец
        function.insert(3.0, 9.0);
        assertEquals(5, function.getCount());
        assertEquals(3.0, function.getX(4), 1e-12);
    }

    @Test
    void testListInsert() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // В начало
        linkedListFunc.insert(-1.0, 1.0);
        assertEquals(6, linkedListFunc.getCount());
        assertEquals(-1.0, linkedListFunc.getX(0), 1e-12);
        assertEquals(1.0, linkedListFunc.getY(0), 1e-12);
        assertEquals(0.0, linkedListFunc.getX(1), 1e-12);

        // В конец
        linkedListFunc.insert(5.0, 25.0);
        assertEquals(7, linkedListFunc.getCount());
        assertEquals(4.0, linkedListFunc.getX(5), 1e-12);
        assertEquals(5.0, linkedListFunc.getX(6), 1e-12);
        assertEquals(25.0, linkedListFunc.getY(6), 1e-12);

        // В середину
        linkedListFunc.insert(1.5, 2.25);
        assertEquals(8, linkedListFunc.getCount());
        assertEquals(1.0, linkedListFunc.getX(2), 1e-12);
        assertEquals(1.5, linkedListFunc.getX(3), 1e-12);
        assertEquals(2.0, linkedListFunc.getX(4), 1e-12);
        assertEquals(2.25, linkedListFunc.getY(3), 1e-12);

        // Вставка с существующим x
        linkedListFunc.insert(2.0, 100.0);
        assertEquals(8, linkedListFunc.getCount()); // Количество не изменилось
        assertEquals(100.0, linkedListFunc.getY(4), 1e-12); // y обновился
    }
}
