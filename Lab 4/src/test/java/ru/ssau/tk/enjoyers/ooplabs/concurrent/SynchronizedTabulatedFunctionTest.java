package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.ssau.tk.enjoyers.ooplabs.functions.*;

import java.util.*;

public class SynchronizedTabulatedFunctionTest {

    @Test
    public void testSingleThread() {
        double[] xValues = {1, 2, 3};
        double[] yValues = {583, 111, 743};
        TabulatedFunction anotherFunction = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(), 1, 1000, 1000);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(function);

        assertEquals(1000, syncFunction.getCount());
        assertEquals(1, syncFunction.getY(23));
        assertEquals(6, syncFunction.getX(5));
        assertEquals(9, syncFunction.indexOfX(10));
        assertEquals(-1, syncFunction.indexOfY(2));
        assertEquals(1, syncFunction.leftBound());
        assertEquals(1000, syncFunction.rightBound());
        assertEquals(1, syncFunction.apply(541));
        assertEquals(583, syncFunction.andThen(anotherFunction).apply(0));
    }

    @Test
    public void testMultiThread() {
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

    @Test
    void testIterator() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 2, 3);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        Iterator<Point> iterator = syncFunction.iterator();
        assertTrue(iterator.hasNext());

        Point point1 = iterator.next();
        assertEquals(0.0, point1.x, 1e-9);
        assertEquals(1.0, point1.y, 1e-9);

        Point point2 = iterator.next();
        assertEquals(1.0, point2.x, 1e-9);
        assertEquals(1.0, point2.y, 1e-9);

        Point point3 = iterator.next();
        assertEquals(2.0, point3.x, 1e-9);
        assertEquals(1.0, point3.y, 1e-9);

        assertFalse(iterator.hasNext());
    }

    @Test
    void testDoSynchronouslyWithReturnValue() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        // Операция с возвращаемым значением
        Double result = syncFunction.doSynchronously(func -> {
            double sum = 0;
            for (int i = 0; i < func.getCount(); i++) {
                sum += func.getY(i);
            }
            return sum;
        });

        assertEquals(11.0, result, 1e-9); // 11 точек * 1.0 = 11.0
    }

    @Test
    void testDoSynchronouslyWithVoid() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        // Операция без возвращаемого значения (Void)
        Void result = syncFunction.doSynchronously(func -> {
            for (int i = 0; i < func.getCount(); i++) {
                func.setY(i, func.getY(i) * 2);
            }
            return null;
        });

        assertNull(result);
        assertEquals(2.0, syncFunction.getY(5), 1e-9);
    }

    @Test
    void testComplexOperation() {
        TabulatedFunction baseFunction = new LinkedListTabulatedFunction(new UnitFunction(), 0, 10, 11);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        // Комплексная операция: находим максимум и умножаем все значения на него
        Double maxValue = syncFunction.doSynchronously(func -> {
            double max = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < func.getCount(); i++) {
                if (func.getY(i) > max) {
                    max = func.getY(i);
                }
            }
            return max;
        });

        assertEquals(1.0, maxValue, 1e-9);

        // Применяем другую операцию с использованием предыдущего результата
        syncFunction.doSynchronously(func -> {
            for (int i = 0; i < func.getCount(); i++) {
                func.setY(i, func.getY(i) * maxValue * 10);
            }
            return null;
        });

        assertEquals(10.0, syncFunction.getY(5), 1e-9);
    }
}
