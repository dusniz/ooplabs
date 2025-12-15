package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AndThenTest {

    private MathFunction identity = new IdentityFunction();
    private MathFunction sqr = new SqrFunction();
    private MathFunction constant = new ConstantFunction(5.0);

    // Базовые тесты композиции
    @Test
    public void testBasicComposition() {
        MathFunction composite = identity.andThen(sqr);

        assertEquals(0.0, composite.apply(0.0), 1e-12);
        assertEquals(1.0, composite.apply(1.0), 1e-12);
        assertEquals(4.0, composite.apply(2.0), 1e-12);
        assertEquals(25.0, composite.apply(5.0), 1e-12);
        assertEquals(9.0, composite.apply(-3.0), 1e-12);
    }

    @Test
    public void testCompositionOrder() {
        // f(x) = x^2, g(x) = x + 1 (через композицию с константой)
        MathFunction addOne = constant.andThen(x -> x + 1); // Всегда 6.0
        MathFunction composite1 = sqr.andThen(addOne); // (x^2) -> 6.0
        MathFunction composite2 = addOne.andThen(sqr); // 6.0 -> 36.0

        assertEquals(6.0, composite1.apply(10.0), 1e-12);
        assertEquals(6.0, composite1.apply(0.0), 1e-12);
        assertEquals(36.0, composite2.apply(100.0), 1e-12);
    }

    // Тесты цепочек композиции
    @Test
    public void testThreeFunctionChain() {
        MathFunction chain = identity.andThen(sqr).andThen(sqr); // x -> x^2 -> x^4

        assertEquals(0.0, chain.apply(0.0), 1e-12);
        assertEquals(1.0, chain.apply(1.0), 1e-12);
        assertEquals(16.0, chain.apply(2.0), 1e-12);
        assertEquals(81.0, chain.apply(3.0), 1e-12);
        assertEquals(625.0, chain.apply(5.0), 1e-12);
    }

    @Test
    public void testComplexChain() {
        MathFunction chain = identity
                .andThen(sqr)                    // x^2
                .andThen(x -> x * 2)      // 2x^2
                .andThen(x -> x + 1)      // 2x^2 + 1
                .andThen(sqr);                   // (2x^2 + 1)^2

        // f(x) = (2x^2 + 1)^2
        assertEquals(1.0, chain.apply(0.0), 1e-12);   // (2*0 + 1)^2 = 1
        assertEquals(9.0, chain.apply(1.0), 1e-12);   // (2*1 + 1)^2 = 9
        assertEquals(81.0, chain.apply(2.0), 1e-12);  // (2*4 + 1)^2 = 81
    }
}
