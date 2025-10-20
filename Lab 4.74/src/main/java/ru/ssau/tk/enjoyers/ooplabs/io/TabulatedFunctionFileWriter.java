package ru.ssau.tk.enjoyers.ooplabs.io;

import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import java.io.*;

public class TabulatedFunctionFileWriter {

    public static void main(String[] args) {
        // Создаём функции для записи
        double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0};

        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        LinkedListTabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

        try (FileWriter arrayWriter = new FileWriter("output/array function.txt");
             BufferedWriter bufferedArrayWriter = new BufferedWriter(arrayWriter);
             FileWriter linkedWriter = new FileWriter("output/linked list function.txt");
             BufferedWriter bufferedLinkedWriter = new BufferedWriter(linkedWriter)) {

            // Записываем функции в файлы
            FunctionsIO.writeTabulatedFunction(bufferedArrayWriter, arrayFunction);
            System.out.println("Функции успешно записаны в файлы:");
            System.out.println("- output/array function.txt");
            FunctionsIO.writeTabulatedFunction(bufferedLinkedWriter, linkedListFunction);
            System.out.println("- output/linked list function.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
