package ru.ssau.tk.enjoyers.ooplabs.concurrent;

import ru.ssau.tk.enjoyers.ooplabs.functions.ConstantFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.LinkedListTabulatedFunction;

public class ReadWriteTaskExecutor {

    public static void main(String[] args) {
        LinkedListTabulatedFunction linListTabFunction = new LinkedListTabulatedFunction(
                new ConstantFunction(-1),
                1,
                1000,
                1000
        );

        Thread readThread = new Thread(new ReadTask(linListTabFunction));
        Thread writeThread = new Thread(new WriteTask(linListTabFunction, 0.5));

        writeThread.start();
        readThread.start();
    }
}
