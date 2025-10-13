package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import java.util.Iterator;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class IteratorTest {

    @Test
    void testArrayTabulatedFunctionIterator() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Test with while loop
        Iterator<Point> iterator = function.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(xValues[count], point.x, 1e-12);
            assertEquals(yValues[count], point.y, 1e-12);
            count++;
        }
        assertEquals(3, count);

        // Test with for-each loop
        count = 0;
        for (Point point : function) {
            assertEquals(xValues[count], point.x, 1e-12);
            assertEquals(yValues[count], point.y, 1e-12);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testLinkedListTabulatedFunctionIterator() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Test with while loop
        Iterator<Point> iterator = function.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(xValues[count], point.x, 1e-12);
            assertEquals(yValues[count], point.y, 1e-12);
            count++;
        }
        assertEquals(3, count);

        // Test with for-each loop
        count = 0;
        for (Point point : function) {
            assertEquals(xValues[count], point.x, 1e-12);
            assertEquals(yValues[count], point.y, 1e-12);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testIteratorNoSuchElementException() {
        double[] xValues = {0.0, 1.0};
        double[] yValues = {0.0, 1.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        iterator.next(); // first element
        iterator.next(); // second element

        assertThrows(NoSuchElementException.class, iterator::next);
    }
}