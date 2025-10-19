package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import org.junit.jupiter.api.Test;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.*;

public class IntegralCalculatorTest {

    @Test
    public void testLinearFunction() throws Exception {
        // f(x) = 2x, S2xdx = x^2, от 0 до 5 = 25
        TabulatedFunction function = new LinkedListTabulatedFunction(x -> 2 * x, 0, 5, 1000);

        IntegralCalculator calculator = new IntegralCalculator(function, 4);
        double result = calculator.calculate();

        assertEquals(25.0, result, 0.1); // Погрешность 0.1
    }

    @Test
    public void testConstantFunction() throws Exception {
        // f(x) = 5, S5dx = 5x, от 0 до 10 = 50
        TabulatedFunction function = new LinkedListTabulatedFunction(x -> 5, 0, 10, 1000);

        IntegralCalculator calculator = new IntegralCalculator(function, 2);
        double result = calculator.calculate();

        assertEquals(50.0, result, 0.1);
    }

    @Test
    public void testExtremeFunction() throws Exception {
        TabulatedFunction function = new LinkedListTabulatedFunction(x -> sqrt(10*pow(x, 5) + 3*pow(x, 3) + 4*pow(x, 2) + x + 1), 1, 9, 100000);

        IntegralCalculator calculator = new IntegralCalculator(function, 1);
        double result = calculator.calculate();

        assertEquals(1985, result, 1);
    }
}
