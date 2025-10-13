package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConstantFunctionTest {

    @Test
    public void givenX_whenApply_thenReturnX() {
        ConstantFunction constantFunction = new ConstantFunction(3.141592);
        assertEquals(3.141592, constantFunction.apply(1111), 0);
    }
}

