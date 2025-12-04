package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import ru.ssau.tk.enjoyers.ooplabs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.UnitFunction;

import java.util.*;

public class MultiplyingTaskExecutor {
    public static void main(String[] args) {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(), 1, 1000, 1000);

        List<Thread> threads = new ArrayList<>();
        List<MultiplyingTask> tasks = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            MultiplyingTask task = new MultiplyingTask(function);
            Thread thread = new Thread(task);
            tasks.add(task);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        while(!tasks.isEmpty()){
            Iterator<MultiplyingTask> iterator = tasks.iterator();
            while (iterator.hasNext()){
                if (iterator.next().isCompleted()) {
                    iterator.remove();
                }
            }
        }

        for (int i = 0; i < function.getCount(); i++) {
            System.out.printf("x = %f, y = %f%n", function.getX(i), function.getY(i));
        }

    }
}
