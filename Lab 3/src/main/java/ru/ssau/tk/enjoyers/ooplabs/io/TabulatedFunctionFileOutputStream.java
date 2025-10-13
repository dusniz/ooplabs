package ru.ssau.tk.enjoyers.ooplabs.io;

import ru.ssau.tk.enjoyers.ooplabs.functions.*;

import java.io.*;

public class TabulatedFunctionFileOutputStream {

    public static void main(String[] args) {
        // Создаём функции для записи
        double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0};

        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        LinkedListTabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

        try (FileOutputStream arrayStream = new FileOutputStream("output/array function.bin");
             BufferedOutputStream bufferedArrayStream = new BufferedOutputStream(arrayStream);
             FileOutputStream linkedStream = new FileOutputStream("output/linked list function.bin");
             BufferedOutputStream bufferedLinkedStream = new BufferedOutputStream(linkedStream)) {

            // Записываем функции в бинарные файлы
            FunctionsIO.writeTabulatedFunction(bufferedArrayStream, arrayFunction);
            FunctionsIO.writeTabulatedFunction(bufferedLinkedStream, linkedListFunction);

            System.out.println("Функции успешно записаны в бинарные файлы:");
            System.out.println("- output/array function.bin");
            System.out.println("- output/linked list function.bin");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}