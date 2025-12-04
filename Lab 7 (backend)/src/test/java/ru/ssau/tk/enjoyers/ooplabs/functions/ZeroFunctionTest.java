package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ZeroFunctionTest {

    @Test
    public void givenX_whenApply_thenReturnX() {
        ZeroFunction zFunction = new ZeroFunction();
        assertEquals(0, zFunction.apply(1984), 0);
    }
}