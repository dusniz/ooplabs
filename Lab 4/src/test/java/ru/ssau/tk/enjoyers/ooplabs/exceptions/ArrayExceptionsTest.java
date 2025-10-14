package ru.ssau.tk.enjoyers.ooplabs.exceptions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.enjoyers.ooplabs.functions.AbstractTabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.ArrayTabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.LinkedListTabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayExceptionsTest {
    @Test
    void testArrayIsNotSortedException() {
        double[] unsortedX = {0.0, 2.0, 1.0, 3.0}; // Не отсортирован

        assertThrows(ArrayIsNotSortedException.class,
                () -> AbstractTabulatedFunction.checkSorted(unsortedX));
    }

    @Test
    void testDifferentLengthOfArraysException() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0}; // Разная длина

        assertThrows(DifferentLengthOfArraysException.class,
                () -> AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues));
    }

    @Test
    void testArrayTabulatedFunctionInvalidConstructor() {
        // Тест слишком короткого массива
        double[] shortX = {0.0};
        double[] shortY = {0.0};

        assertThrows(IllegalArgumentException.class,
                () -> new ArrayTabulatedFunction(shortX, shortY));

        // Тест неотсортированного массива
        double[] unsortedX = {2.0, 1.0, 3.0};
        double[] unsortedY = {4.0, 1.0, 9.0};

        assertThrows(ArrayIsNotSortedException.class,
                () -> new ArrayTabulatedFunction(unsortedX, unsortedY));

        // Тест разных длин массивов
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0};

        assertThrows(DifferentLengthOfArraysException.class,
                () -> new ArrayTabulatedFunction(xValues, yValues));
    }

    @Test
    void testLinkedListTabulatedFunctionInvalidConstructor() {
        // Тест слишком короткого массива
        double[] shortX = {0.0};
        double[] shortY = {0.0};

        assertThrows(IllegalArgumentException.class,
                () -> new LinkedListTabulatedFunction(shortX, shortY));

        // Тест неотсортированного массива
        double[] unsortedX = {2.0, 1.0, 3.0};
        double[] unsortedY = {4.0, 1.0, 9.0};

        assertThrows(ArrayIsNotSortedException.class,
                () -> new LinkedListTabulatedFunction(unsortedX, unsortedY));
    }

    @Test
    void testInvalidIndexAccess() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        LinkedListTabulatedFunction linkedFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // Тест отрицательного индекса
        assertThrows(IllegalArgumentException.class, () -> arrayFunc.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.getX(-1));

        // Тест индекса больше размера
        assertThrows(IllegalArgumentException.class, () -> arrayFunc.getX(3));
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.getX(3));

        // Тест setY с невалидным индексом
        assertThrows(IllegalArgumentException.class, () -> arrayFunc.setY(-1, 10.0));
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.setY(-1, 10.0));
    }

    @Test
    void testFloorIndexOfXWithInvalidX() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        LinkedListTabulatedFunction linkedFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // Тест x меньше левой границы
        assertThrows(IllegalArgumentException.class, () -> arrayFunc.floorIndexOfX(0.0));
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.floorIndexOfX(0.0));
    }

    @Test
    void testInterpolationException() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(1.0, function.apply(1.0), 1e-12);
        assertEquals(4.0, function.apply(2.0), 1e-12);
        assertEquals(9.0, function.apply(3.0), 1e-12);
        assertEquals(2.5, function.apply(1.5), 1e-12);
        assertEquals(6.5, function.apply(2.5), 1e-12);

        assertThrows(InterpolationException.class,
                () -> function.interpolate(1000, 1.0, 3.0, 1.0, 9.0));
    }
}
