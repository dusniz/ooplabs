package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DecoratorTest {

    @Test
    void testStrictTabulatedFunction() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        StrictTabulatedFunction strictFunc = new StrictTabulatedFunction(arrayFunc);

        assertEquals(3, strictFunc.getCount());
        assertEquals(1.0, strictFunc.getX(1), 1e-12);
        assertEquals(4.0, strictFunc.getY(2), 1e-12);
        assertEquals(0.0, strictFunc.leftBound(), 1e-12);
        assertEquals(2.0, strictFunc.rightBound(), 1e-12);

        assertEquals(1.0, strictFunc.apply(1.0), 1e-12);
        assertEquals(4.0, strictFunc.apply(2.0), 1e-12);

        assertThrows(UnsupportedOperationException.class, () -> strictFunc.apply(0.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunc.apply(1.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunc.apply(-1.0));
        assertThrows(UnsupportedOperationException.class, () -> strictFunc.apply(3.0));
    }

    @Test
    void testUnmodifiableTabulatedFunction() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        UnmodifiableTabulatedFunction unmodifiableFunc = new UnmodifiableTabulatedFunction(arrayFunc);

        assertEquals(3, unmodifiableFunc.getCount());
        assertEquals(1.0, unmodifiableFunc.getX(1), 1e-12);
        assertEquals(4.0, unmodifiableFunc.getY(2), 1e-12);
        assertEquals(0.0, unmodifiableFunc.leftBound(), 1e-12);
        assertEquals(2.0, unmodifiableFunc.rightBound(), 1e-12);

        assertEquals(1.0, unmodifiableFunc.apply(1.0), 1e-12);
        assertEquals(2.5, unmodifiableFunc.apply(1.5), 1e-12);
        assertEquals(5.5, unmodifiableFunc.apply(2.5), 1e-12);

        assertThrows(UnsupportedOperationException.class, () -> unmodifiableFunc.setY(1, 10.0));
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableFunc.setY(0, 5.0));
    }

    @Test
    void testStrictWithLinkedList() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        LinkedListTabulatedFunction linkedFunc = new LinkedListTabulatedFunction(xValues, yValues);
        StrictTabulatedFunction strictFunc = new StrictTabulatedFunction(linkedFunc);

        assertEquals(1.0, strictFunc.apply(1.0), 1e-12);
        assertThrows(UnsupportedOperationException.class, () -> strictFunc.apply(0.5));
    }

    @Test
    void testUnmodifiableWithLinkedList() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        LinkedListTabulatedFunction linkedFunc = new LinkedListTabulatedFunction(xValues, yValues);
        UnmodifiableTabulatedFunction unmodifiableFunc = new UnmodifiableTabulatedFunction(linkedFunc);

        assertEquals(1.0, unmodifiableFunc.apply(1.0), 1e-12);
        assertEquals(2.5, unmodifiableFunc.apply(1.5), 1e-12);
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableFunc.setY(1, 10.0));
    }

    @Test
    void testDoubleDecoration() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);

        StrictTabulatedFunction strictFunc = new StrictTabulatedFunction(arrayFunc);
        UnmodifiableTabulatedFunction doubleWrapped = new UnmodifiableTabulatedFunction(strictFunc);

        assertEquals(1.0, doubleWrapped.apply(1.0), 1e-12);
        assertThrows(UnsupportedOperationException.class, () -> doubleWrapped.apply(0.5));
        assertThrows(UnsupportedOperationException.class, () -> doubleWrapped.setY(1, 10.0));

        UnmodifiableTabulatedFunction unmodifiableFunc = new UnmodifiableTabulatedFunction(arrayFunc);
        StrictTabulatedFunction otherOrder = new StrictTabulatedFunction(unmodifiableFunc);

        assertEquals(1.0, otherOrder.apply(1.0), 1e-12);
        assertThrows(UnsupportedOperationException.class, () -> otherOrder.apply(0.5));
        assertThrows(UnsupportedOperationException.class, () -> otherOrder.setY(1, 10.0));
    }

    @Test
    void testIteratorInDecorators() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);

        StrictTabulatedFunction strictFunc = new StrictTabulatedFunction(arrayFunc);
        UnmodifiableTabulatedFunction unmodifiableFunc = new UnmodifiableTabulatedFunction(arrayFunc);

        int count = 0;
        for (Point point : strictFunc) {
            assertEquals(xValues[count], point.x, 1e-12);
            assertEquals(yValues[count], point.y, 1e-12);
            count++;
        }
        assertEquals(3, count);

        count = 0;
        for (Point point : unmodifiableFunc) {
            assertEquals(xValues[count], point.x, 1e-12);
            assertEquals(yValues[count], point.y, 1e-12);
            count++;
        }
        assertEquals(3, count);
    }
}