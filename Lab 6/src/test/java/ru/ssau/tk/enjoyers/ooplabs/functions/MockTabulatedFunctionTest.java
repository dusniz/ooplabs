package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MockTabulatedFunctionTest {

    @Test
    public void testApply() {
        MockTabulatedFunction mock = new MockTabulatedFunction();

        // Интерполяция
        assertEquals(0.6, mock.apply(0.3), 1e-12);

        // Экстраполяция слева
        assertEquals(-1.0, mock.apply(-0.5), 1e-12);

        // Экстраполяция справа
        assertEquals(3.0, mock.apply(1.5), 1e-12);
    }

    @Test
    public void testGetters() {
        MockTabulatedFunction mock = new MockTabulatedFunction();

        assertEquals(2, mock.getCount());
        assertEquals(0.0, mock.getX(0), 1e-12);
        assertEquals(1.0, mock.getX(1), 1e-12);
        assertEquals(0.0, mock.getY(0), 1e-12);
        assertEquals(2.0, mock.getY(1), 1e-12);
    }

    @Test
    public void testBounds() {
        MockTabulatedFunction mock = new MockTabulatedFunction();

        assertEquals(0.0, mock.leftBound(), 1e-12);
        assertEquals(1.0, mock.rightBound(), 1e-12);
    }

    @Test
    public void testIndexOf() {
        MockTabulatedFunction mock = new MockTabulatedFunction();

        assertEquals(0, mock.indexOfX(0.0));
        assertEquals(1, mock.indexOfX(1.0));
        assertEquals(-1, mock.indexOfX(0.5));

        assertEquals(0, mock.indexOfY(0.0));
        assertEquals(1, mock.indexOfY(2.0));
        assertEquals(-1, mock.indexOfY(1.0));
    }

    @Test
    public void testFloorIndexOfX() {
        MockTabulatedFunction mock = new MockTabulatedFunction();

        assertEquals(0, mock.floorIndexOfX(-1.0));
        assertEquals(0, mock.floorIndexOfX(0.0));
        assertEquals(0, mock.floorIndexOfX(0.3));
        assertEquals(1, mock.floorIndexOfX(0.7));
        assertEquals(2, mock.floorIndexOfX(2.0));
    }

    @Test
    public void testMiscellaneous() {
        MockTabulatedFunction mock = new MockTabulatedFunction();

        assertThrows(UnsupportedOperationException.class, () -> mock.setY(0,1));
        assertThrows(UnsupportedOperationException.class, mock::iterator);
    }
}
