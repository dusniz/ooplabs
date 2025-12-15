package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SqrFunctionTest {

    @Test
    public void givenX_whenApply_thenReturnX() {
        SqrFunction sqrFunction = new SqrFunction();
        assertEquals(5.29, sqrFunction.apply(2.3), 0.1);
    }
}
