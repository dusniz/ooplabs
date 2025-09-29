package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InsertableTest {

    @Test
    void testInsert() {
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
}
