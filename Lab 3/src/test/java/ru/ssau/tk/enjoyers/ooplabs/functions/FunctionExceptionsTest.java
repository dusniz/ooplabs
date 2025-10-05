package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.enjoyers.ooplabs.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk.enjoyers.ooplabs.exceptions.DifferentLengthOfArraysException;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionExceptionsTest {
    @Test
    void testArrayIsNotSortedException() {
        double[] unsortedX = {0.0, 2.0, 1.0, 3.0}; // Не отсортирован
        double[] yValues = {0.0, 4.0, 1.0, 9.0};

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
        // Тест неотсортированного массива
        double[] unsortedX = {2.0, 1.0, 3.0};
        double[] unsortedY = {4.0, 1.0, 9.0};

        assertThrows(ArrayIsNotSortedException.class,
                () -> new LinkedListTabulatedFunction(unsortedX, unsortedY));
    }
}
