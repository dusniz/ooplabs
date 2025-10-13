package ru.ssau.tk.enjoyers.ooplabs.io;

import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import ru.ssau.tk.enjoyers.ooplabs.functions.factory.*;
import ru.ssau.tk.enjoyers.ooplabs.operations.TabulatedDifferentialOperator;
import java.io.*;

public class TabulatedFunctionFileInputStream {

    public static void main(String[] args) {
        TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
        TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();

        // Чтение из бинарного файла
        try (FileInputStream fileStream = new FileInputStream("input/binary function.bin");
             BufferedInputStream bufferedStream = new BufferedInputStream(fileStream)) {

            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(bufferedStream, arrayFactory);
            System.out.println("Функция из бинарного файла:");
            System.out.println(arrayFunction);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Чтение из консоли
        System.out.println("Введите размер и значения функции:");

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            BufferedReader consoleReader = new BufferedReader(inputStreamReader);

            TabulatedFunction consoleFunction = FunctionsIO.readTabulatedFunction(consoleReader, linkedListFactory);

            TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();
            TabulatedFunction derivative = differentialOperator.derive(consoleFunction);

            System.out.println("Производная введённой функции:");
            System.out.println(derivative);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}