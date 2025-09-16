package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.Assert;
import org.junit.Test;

public class ZeroFunctionTest {

    @Test
    public void givenX_whenApply_thenReturnX() {
        ZeroFunction zFunction = new ZeroFunction();
        Assert.assertEquals(0, zFunction.apply(1984), 0);
    }
}