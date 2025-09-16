package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.Assert;
import org.junit.Test;

public class SqrFunctionTest {

    @Test
    public void givenX_whenApply_thenReturnX() {
        SqrFunction sqrFunction = new SqrFunction();
        Assert.assertEquals(5.29, sqrFunction.apply(2.3), 0.1);
    }
}
