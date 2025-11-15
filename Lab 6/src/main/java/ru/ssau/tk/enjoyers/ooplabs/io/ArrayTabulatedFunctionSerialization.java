package ru.ssau.tk.enjoyers.ooplabs.io;

import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import ru.ssau.tk.enjoyers.ooplabs.operations.TabulatedDifferentialOperator;

import java.io.*;

public class ArrayTabulatedFunctionSerialization {

    public static void main(String[] args) {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

        ArrayTabulatedFunction originalFunction = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction firstDerivative = operator.derive(originalFunction);
        TabulatedFunction secondDerivative = operator.derive(firstDerivative);

        // Сериализация
        try (FileOutputStream fileStream = new FileOutputStream("output/serialized array functions.bin");
             BufferedOutputStream bufferedStream = new BufferedOutputStream(fileStream)) {

            FunctionsIO.serialize(bufferedStream, originalFunction);
            FunctionsIO.serialize(bufferedStream, firstDerivative);
            FunctionsIO.serialize(bufferedStream, secondDerivative);

            System.out.println("Функции успешно сериализованы");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}