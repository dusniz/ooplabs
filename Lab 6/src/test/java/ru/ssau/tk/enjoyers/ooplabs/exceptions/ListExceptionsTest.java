package ru.ssau.tk.enjoyers.ooplabs.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.ssau.tk.enjoyers.ooplabs.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.Point;
import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.UnitFunction;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ListExceptionsTest {

    @Test
    public void testLinkedListTabulatedFunctionIllegalArgumentException() {
        double[] xValues = {1.0};
        double[] yValues = {1.0};

        assertThrows(IllegalArgumentException.class,
                () -> new LinkedListTabulatedFunction(xValues, yValues));

        assertThrows(IllegalArgumentException.class,
                () -> new LinkedListTabulatedFunction(new UnitFunction(), 1, 1, 1));
    }

    @Test
    public void testLinkedListTabulatedFunctionNoSuchElementException() {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(), 0, 2, 2);

        Iterator<Point> iterator = function.iterator();
        iterator.next();
        iterator.next();

        assertThrows(NoSuchElementException.class, iterator::next);
    }
}

