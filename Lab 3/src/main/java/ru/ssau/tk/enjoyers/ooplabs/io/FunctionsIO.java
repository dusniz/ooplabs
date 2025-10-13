package ru.ssau.tk.enjoyers.ooplabs.io;

import ru.ssau.tk.enjoyers.ooplabs.functions.Point;
import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;
import ru.ssau.tk.enjoyers.ooplabs.functions.factory.TabulatedFunctionFactory;
import java.io.*;
import java.text.*;
import java.util.Locale;
import ru.ssau.tk.enjoyers.ooplabs.exceptions.ArrayIsNotSortedException;

public final class FunctionsIO {

    private FunctionsIO() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function) throws IOException {
        PrintWriter printWriter = new PrintWriter(writer);
        printWriter.println(function.getCount());

        for (Point point : function) {
            printWriter.printf("%f %f%n", point.x, point.y);
        }

        writer.flush();
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(function.getCount());

        for (Point point : function) {
            dataOutputStream.writeDouble(point.x);
            dataOutputStream.writeDouble(point.y);
        }

        outputStream.flush();
    }

    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) throws IOException {
        try {
            String countLine = reader.readLine();
            if (countLine == null) {
                throw new IOException("File is empty");
            }

            int count = Integer.parseInt(countLine.trim());
            double[] xValues = new double[count];
            double[] yValues = new double[count];

            NumberFormat numberFormat = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            for (int i = 0; i < count; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("Unexpected end of file");
                }

                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    throw new IOException("Invalid file format");
                }

                try {
                    xValues[i] = numberFormat.parse(parts[0]).doubleValue();
                    yValues[i] = numberFormat.parse(parts[1]).doubleValue();
                } catch (ParseException e) {
                    throw new IOException("Error parsing numbers", e);
                }
            }

            return factory.create(xValues, yValues);

        } catch (NumberFormatException e) {
            throw new IOException("Error parsing count", e);
        }
    }

    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        int count = dataInputStream.readInt();

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) {
            try {
                xValues[i] = dataInputStream.readDouble();
                yValues[i] = dataInputStream.readDouble();
            } catch (EOFException e) {
                throw new IOException("Unexpected end of binary file", e);
            }
        }

        // ВАЖНО: Проверяем и сортируем данные
        if (!isSorted(xValues)) {
            System.err.println("Warning: X values are not sorted, sorting...");
            sortArrays(xValues, yValues);
        }

        try {
            return factory.create(xValues, yValues);
        } catch (ArrayIsNotSortedException e) {
            // Если всё ещё не отсортировано, пробуем исправить
            sortArrays(xValues, yValues);
            return factory.create(xValues, yValues);
        }
    }

    // Вспомогательные методы для проверки и сортировки
    private static boolean isSorted(double[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] <= array[i - 1]) {
                return false;
            }
        }
        return true;
    }

    private static void sortArrays(double[] xValues, double[] yValues) {
        // Сортируем пары (x, y) по x значениям
        for (int i = 0; i < xValues.length; i++) {
            for (int j = i + 1; j < xValues.length; j++) {
                if (xValues[i] > xValues[j]) {
                    // Меняем местами x значения
                    double tempX = xValues[i];
                    xValues[i] = xValues[j];
                    xValues[j] = tempX;

                    // Меняем местами соответствующие y значения
                    double tempY = yValues[i];
                    yValues[i] = yValues[j];
                    yValues[j] = tempY;
                }
            }
        }
    }

    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
        objectOutputStream.writeObject(function);
        stream.flush();
    }
}
