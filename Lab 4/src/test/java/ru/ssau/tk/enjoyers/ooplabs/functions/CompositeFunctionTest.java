package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {
    @Test
    void testApply() {
        MathFunction sqr = new SqrFunction();
        MathFunction identity = new IdentityFunction();
        CompositeFunction composite = new CompositeFunction(identity, sqr);

        assertEquals(4.0, composite.apply(2.0), 1e-12);
        assertEquals(0.0, composite.apply(0.0), 1e-12);
    }

}