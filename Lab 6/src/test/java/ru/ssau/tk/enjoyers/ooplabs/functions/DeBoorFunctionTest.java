package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeBoorFunctionTest {

    @Test
    public void testApply() {
        double[] knots = {0, 0, 0, 1, 2, 2, 2};
        double[] controlPoints = {0, 1, 0};
        int degree = 2;

        DeBoorFunction deBoor = new DeBoorFunction(knots, controlPoints, degree);

        double result1 = deBoor.apply(0.5);
        double result2 = deBoor.apply(1.0);
        double result3 = deBoor.apply(1.5);

        assertTrue(result1 >= 0 && result1 <= 1);
        assertTrue(result2 >= 0 && result2 <= 1);
        assertTrue(result3 >= 0 && result3 <= 1);
    }

}