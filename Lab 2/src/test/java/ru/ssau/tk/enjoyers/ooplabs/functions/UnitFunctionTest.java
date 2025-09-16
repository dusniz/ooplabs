package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.Assert;
import org.junit.Test;

public class UnitFunctionTest {

    @Test
    public void givenX_whenApply_thenReturnX() {
        UnitFunction uFunction = new UnitFunction();
        Assert.assertEquals(1, uFunction.apply(1488), 0);
    }
}