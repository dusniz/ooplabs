package ru.ssau.tk.enjoyers.ooplabs.io;

import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import ru.ssau.tk.enjoyers.ooplabs.functions.factory.*;

import java.io.*;

public class TabulatedFunctionFileReader {

    public static void main(String[] args) {
        TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
        TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();

        try (FileReader fileReader1 = new FileReader("input/function.txt");
             BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
             FileReader fileReader2 = new FileReader("input/function.txt");
             BufferedReader bufferedReader2 = new BufferedReader(fileReader2)) {

            // Читаем функции из одного файла, но создаём разные типы
            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(bufferedReader1, arrayFactory);
            TabulatedFunction linkedListFunction = FunctionsIO.readTabulatedFunction(bufferedReader2, linkedListFactory);

            System.out.println("ArrayTabulatedFunction:");
            System.out.println(arrayFunction);

            System.out.println("LinkedListTabulatedFunction:");
            System.out.println(linkedListFunction);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}