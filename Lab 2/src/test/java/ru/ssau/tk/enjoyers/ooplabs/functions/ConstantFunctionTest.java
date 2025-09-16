package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.Assert;
import org.junit.Test;

public class ConstantFunctionTest {

    @Test
    public void givenX_whenApply_thenReturnX() {
        ConstantFunction constantFunction = new ConstantFunction(3.141592);
        Assert.assertEquals(3.141592, constantFunction.apply(1111), 0);
    }
}

