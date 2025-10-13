package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IdentityFunctionTest {

    @Test
    public void givenX_whenApply_thenReturnX() {
        double expectedX = 10.58;
        IdentityFunction identityFunction = new IdentityFunction();
        double actualX = identityFunction.apply(expectedX);
        double delta = 0.0000001;
        assertEquals(expectedX, actualX, delta);
    }
}