package ru.ssau.tk.enjoyers.ooplabs.io;

import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import ru.ssau.tk.enjoyers.ooplabs.operations.TabulatedDifferentialOperator;

import java.io.*;

public class LinkedListTabulatedFunctionSerialization {

    public static void main(String[] args) {
        // Создаём функцию и её производные
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

        LinkedListTabulatedFunction originalFunction = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction firstDerivative = operator.derive(originalFunction);
        TabulatedFunction secondDerivative = operator.derive(firstDerivative);

        // Сериализация
        try (FileOutputStream fileStream = new FileOutputStream("output/serialized linked list functions.bin");
             BufferedOutputStream bufferedStream = new BufferedOutputStream(fileStream)) {

            FunctionsIO.serialize(bufferedStream, originalFunction);
            FunctionsIO.serialize(bufferedStream, firstDerivative);
            FunctionsIO.serialize(bufferedStream, secondDerivative);

            System.out.println("Функции успешно сериализованы");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Десериализация
        try (FileInputStream fileStream = new FileInputStream("output/serialized linked list functions.bin");
             BufferedInputStream bufferedStream = new BufferedInputStream(fileStream)) {

            TabulatedFunction deserializedOriginal = FunctionsIO.deserialize(bufferedStream);
            TabulatedFunction deserializedFirstDeriv = FunctionsIO.deserialize(bufferedStream);
            TabulatedFunction deserializedSecondDeriv = FunctionsIO.deserialize(bufferedStream);

            System.out.println("Исходная функция:");
            System.out.println(deserializedOriginal);

            System.out.println("Первая производная:");
            System.out.println(deserializedFirstDeriv);

            System.out.println("Вторая производная:");
            System.out.println(deserializedSecondDeriv);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}