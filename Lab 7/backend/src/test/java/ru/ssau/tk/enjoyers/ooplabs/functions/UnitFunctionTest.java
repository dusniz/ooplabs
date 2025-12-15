package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnitFunctionTest {

    @Test
    public void givenX_whenApply_thenReturnX() {
        UnitFunction uFunction = new UnitFunction();
        assertEquals(1, uFunction.apply(1488), 0);
    }
}