package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.Assert;
import org.junit.Test;

public class SqrFunctionTest {

    @Test
    public void givenX_whenApply_thenReturnX() {
        double expectedX = 5.29;
        SqrFunction sqrFunction = new SqrFunction();
        double actualX = sqrFunction.apply(2.3);
        Assert.assertEquals(expectedX, actualX, 0.1);
    }
}
