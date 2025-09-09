package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.Assert;
import org.junit.Test;

public class IdentityFunctionTest {

    @Test
    public void givenX_whenApply_thenReturnX() {
        double expectedX = 10.58;
        IdentityFunction identityFunction = new IdentityFunction();
        double actualX = identityFunction.apply(expectedX);
        Assert.assertEquals(expectedX, actualX);
    }
}