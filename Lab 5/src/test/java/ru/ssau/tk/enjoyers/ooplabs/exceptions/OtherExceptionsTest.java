package ru.ssau.tk.enjoyers.ooplabs.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.ssau.tk.enjoyers.ooplabs.functions.ArrayTabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.MathFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.NewtonMethod;
import ru.ssau.tk.enjoyers.ooplabs.operations.LeftSteppingDifferentialOperator;
import ru.ssau.tk.enjoyers.ooplabs.operations.TabulatedFunctionOperationService;

public class OtherExceptionsTest {

    @Test
    public void testNewtonMethodException() {
        double a = 25.0;
        MathFunction f = x -> 1;
        MathFunction df = x -> 0;

        NewtonMethod newton = new NewtonMethod(f, df, 1e-12, 3);

        assertThrows(ArithmeticException.class, () -> newton.apply(100));
    }

    @Test
    public void testInvalidStep() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(1);

        assertThrows(IllegalArgumentException.class, () -> operator.setStep(0));
        assertThrows(IllegalArgumentException.class, () -> operator.setStep(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> operator.setStep(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(0));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(Double.POSITIVE_INFINITY));
    }

    @Test
    public void testServiceDivisionByZero() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {0.0, 1.0, 2.0};
        double[] yValues2 = {0.0, 0.0, 0.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        assertThrows(ArithmeticException.class, () -> service.divide(func1, func2));
    }

    @Test
    public void testInconsistentFunctionsException() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {0.0, 1.0};
        double[] yValues2 = {1.0, 2.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        assertThrows(InconsistentFunctionsException.class, () -> service.add(func1, func2));
    }

    @Test
    public void testDifferentXValuesException() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {0.0, 1.0, 3.0};
        double[] yValues2 = {1.0, 2.0, 3.0};
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        assertThrows(InconsistentFunctionsException.class, () -> service.add(func1, func2));
    }


    @Test
    public void testMiscCases() {
        assertThrows(InconsistentFunctionsException.class, () -> { throw new InconsistentFunctionsException(); });
    }
}
