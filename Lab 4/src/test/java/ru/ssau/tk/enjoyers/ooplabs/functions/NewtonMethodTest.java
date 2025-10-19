package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NewtonMethodTest {

    @Test
    public void testSquareRoot() {
        // f(x) = x^2 - a, ищем корень из a
        double a = 25.0;
        MathFunction f = x -> x * x - a;
        MathFunction df = x -> 2 * x;

        NewtonMethod newton = new NewtonMethod(f, df, 1e-12, 100);

        assertEquals(5.0, newton.apply(10.0), 1e-6);
        assertEquals(5.0, newton.apply(1.0), 1e-6);
        assertEquals(5.0, newton.apply(8.0), 1e-6);
    }

    @Test
    public void testSquareRootFewIterations() {
        // f(x) = x^2 - a, ищем корень из a
        double a = 25.0;
        MathFunction f = x -> x * x - a;
        MathFunction df = x -> 2 * x;

        NewtonMethod newton = new NewtonMethod(f, df, 1e-12, 3);

        assertEquals(5.0, newton.apply(10.0), 1);
        assertEquals(5.0, newton.apply(1.0), 1);
        assertEquals(5.0, newton.apply(8.0), 1);
        assertEquals(5.0, newton.apply(100.0), 10);
    }
}