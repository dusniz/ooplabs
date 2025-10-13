package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.enjoyers.ooplabs.operations.*;
import ru.ssau.tk.enjoyers.ooplabs.exceptions.InconsistentFunctionsException;
import static org.junit.jupiter.api.Assertions.*;

class OperationsTest {

    @Test
    void testAsPoints() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(3, points.length);
        assertEquals(0.0, points[0].x, 1e-12);
        assertEquals(0.0, points[0].y, 1e-12);
        assertEquals(1.0, points[1].x, 1e-12);
        assertEquals(1.0, points[1].y, 1e-12);
        assertEquals(2.0, points[2].x, 1e-12);
        assertEquals(4.0, points[2].y, 1e-12);
    }

    @Test
    void testAddOperations() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {0.0, 1.0, 2.0};
        double[] yValues2 = {1.0, 2.0, 3.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.add(func1, func2);

        assertEquals(3, result.getCount());
        assertEquals(1.0, result.getY(0), 1e-12); // 0 + 1 = 1
        assertEquals(3.0, result.getY(1), 1e-12); // 1 + 2 = 3
        assertEquals(7.0, result.getY(2), 1e-12); // 4 + 3 = 7
    }

    @Test
    void testSubtractOperations() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {5.0, 3.0, 1.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {0.0, 1.0, 2.0};
        double[] yValues2 = {2.0, 1.0, 0.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.subtract(func1, func2);

        assertEquals(3, result.getCount());
        assertEquals(3.0, result.getY(0), 1e-12); // 5 - 2 = 3
        assertEquals(2.0, result.getY(1), 1e-12); // 3 - 1 = 2
        assertEquals(1.0, result.getY(2), 1e-12); // 1 - 0 = 1
    }

    @Test
    void testMultiplyOperations() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {2.0, 3.0, 4.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {0.0, 1.0, 2.0};
        double[] yValues2 = {1.0, 2.0, 3.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.multiply(func1, func2);

        assertEquals(3, result.getCount());
        assertEquals(2.0, result.getY(0), 1e-12); // 2 * 1 = 2
        assertEquals(6.0, result.getY(1), 1e-12); // 3 * 2 = 6
        assertEquals(12.0, result.getY(2), 1e-12); // 4 * 3 = 12
    }

    @Test
    void testDivideOperations() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {6.0, 8.0, 10.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {2.0, 4.0, 5.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.divide(func1, func2);

        assertEquals(3, result.getCount());
        assertEquals(3.0, result.getY(0), 1e-12); // 6 / 2 = 3
        assertEquals(2.0, result.getY(1), 1e-12); // 8 / 4 = 2
        assertEquals(2.0, result.getY(2), 1e-12); // 10 / 5 = 2
    }

    @Test
    void testInconsistentFunctionsException() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {0.0, 1.0}; // Different length
        double[] yValues2 = {1.0, 2.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        assertThrows(InconsistentFunctionsException.class, () -> service.add(func1, func2));
    }

    @Test
    void testDifferentXValuesException() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {0.0, 1.0, 3.0}; // Different x values
        double[] yValues2 = {1.0, 2.0, 3.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        assertThrows(InconsistentFunctionsException.class, () -> service.add(func1, func2));
    }
}
