package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.ssau.tk.enjoyers.ooplabs.functions.CompositeFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.UnitFunction;
import java.util.*;

public class SynchronizedTabulatedFunctionTest {


    @Test
    void testSingleThread() {
        double[] xValues = {1, 2, 3};
        double[] yValues = {583, 111, 743};
        TabulatedFunction anotherFunction = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(), 1, 1000, 1000);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(function);

        assertEquals(1000, syncFunction.getCount());
        assertEquals(1, syncFunction.getY(23));
        assertEquals(1, syncFunction.apply(541));
        assertEquals(583, syncFunction.andThen(anotherFunction).apply(0));
    }

    @Test
    void testMultiThread() {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(), 1, 1000, 1000);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(function);

        List<Thread> threads = new ArrayList<>();
        List<SynchronizedTabulatedFunctionTask> tasks = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            SynchronizedTabulatedFunctionTask task = new SynchronizedTabulatedFunctionTask(syncFunction);
            Thread thread = new Thread(task);
            tasks.add(task);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        while(!tasks.isEmpty()){
            Iterator<SynchronizedTabulatedFunctionTask> iterator = tasks.iterator();
            while (iterator.hasNext()){
                if (iterator.next().isCompleted()) {
                    iterator.remove();
                }
            }
        }

        List<Double> expected = new ArrayList<Double>();
        for (int i = 0; i < syncFunction.getCount(); i++) {
            expected.add(1024.0);
        }

        List<Double> actual = new ArrayList<Double>();
        for (int i = 0; i < syncFunction.getCount(); i++) {
            actual.add(syncFunction.getY(i));
        }

        assertEquals(expected, actual);
    }
}
