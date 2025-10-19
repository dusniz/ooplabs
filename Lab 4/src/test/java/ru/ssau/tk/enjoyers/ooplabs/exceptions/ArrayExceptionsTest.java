package ru.ssau.tk.enjoyers.ooplabs.exceptions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.enjoyers.ooplabs.functions.*;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayExceptionsTest {
    @Test
    void testArrayIsNotSortedException() {
        double[] unsortedX = {0.0, 2.0, 1.0, 3.0}; // Не отсортирован

        assertThrows(ArrayIsNotSortedException.class,
                () -> AbstractTabulatedFunction.checkSorted(unsortedX));

        assertThrows(ArrayIsNotSortedException.class,
                () -> { throw new ArrayIsNotSortedException(); });
    }

    @Test
    void testDifferentLengthOfArraysException() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0}; // Разная длина

        assertThrows(DifferentLengthOfArraysException.class,
                () -> AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues));

        assertThrows(DifferentLengthOfArraysException.class,
                () -> { throw new DifferentLengthOfArraysException(); });
    }

    @Test
    void testArrayTabulatedFunctionInvalidConstructor() {
        // Тест слишком короткого массива
        double[] shortX = {0.0};
        double[] shortY = {0.0};

        assertThrows(IllegalArgumentException.class,
                () -> new ArrayTabulatedFunction(shortX, shortY));
        assertThrows(IllegalArgumentException.class,
                () -> new ArrayTabulatedFunction(new UnitFunction(), 1, 1, 1));

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
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.getX(-19));
        assertThrows(IllegalArgumentException.class, () -> arrayFunc.getY(-228));
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.getY(-6));
        assertThrows(IllegalArgumentException.class, () -> arrayFunc.setY(-1488, 10.0));
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.setY(-10, 10.0));
        assertThrows(IllegalArgumentException.class, () -> arrayFunc.remove(-123));
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.remove(-111111));


        // Тест индекса больше размера
        assertThrows(IllegalArgumentException.class, () -> arrayFunc.getX(3));
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.getX(4));
        assertThrows(IllegalArgumentException.class, () -> arrayFunc.getY(911));
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.getY(8));
        assertThrows(IllegalArgumentException.class, () -> arrayFunc.setY(5, 10.0));
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.setY(6, 10.0));
        assertThrows(IllegalArgumentException.class, () -> arrayFunc.remove(91232));
        assertThrows(IllegalArgumentException.class, () -> linkedFunc.remove(25565));
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
        assertThrows(InterpolationException.class,
                () -> function.interpolate(-1000, 1.0, 3.0, 1.0, 9.0));

        assertThrows(InterpolationException.class,
                () -> { throw new InterpolationException(); });
    }
}
