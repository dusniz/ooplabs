package ru.ssau.tk.enjoyers.ooplabs.io;

import ru.ssau.tk.enjoyers.ooplabs.functions.ArrayTabulatedFunction;
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

    public static TabulatedFunction deserialize(BufferedInputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(stream);
        return (TabulatedFunction) objectInputStream.readObject();
    }

    public static void serializeXml(BufferedWriter writer, ArrayTabulatedFunction function) throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.newLine();
        writer.write("<ArrayTabulatedFunction>");
        writer.newLine();
        writer.write("  <count>" + function.getCount() + "</count>");
        writer.newLine();
        writer.write("  <xValues>");

        for (int i = 0; i < function.getCount(); i++) {
            writer.write(String.format(Locale.US, "%.6f", function.getX(i)));
            if (i < function.getCount() - 1) {
                writer.write(",");
            }
        }

        writer.write("</xValues>");
        writer.newLine();
        writer.write("  <yValues>");

        for (int i = 0; i < function.getCount(); i++) {
            writer.write(String.format(Locale.US, "%.6f", function.getY(i)));
            if (i < function.getCount() - 1) {
                writer.write(",");
            }
        }

        writer.write("</yValues>");
        writer.newLine();
        writer.write("</ArrayTabulatedFunction>");
        writer.newLine();
        writer.flush();
    }

    public static ArrayTabulatedFunction deserializeXml(BufferedReader reader) throws IOException {
        int count = 0;
        double[] xValues = null;
        double[] yValues = null;

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("<count>")) {
                count = Integer.parseInt(line.replace("<count>", "").replace("</count>", ""));
            } else if (line.startsWith("<xValues>")) {
                String xData = line.replace("<xValues>", "").replace("</xValues>", "");
                String[] xParts = xData.split(",");
                xValues = new double[count];
                for (int i = 0; i < count; i++) {
                    xValues[i] = Double.parseDouble(xParts[i]);
                }
            } else if (line.startsWith("<yValues>")) {
                String yData = line.replace("<yValues>", "").replace("</yValues>", "");
                String[] yParts = yData.split(",");
                yValues = new double[count];
                for (int i = 0; i < count; i++) {
                    yValues[i] = Double.parseDouble(yParts[i]);
                }
            }
        }

        if (xValues != null && yValues != null) {
            return new ArrayTabulatedFunction(xValues, yValues);
        } else {
            throw new IOException("Invalid XML format");
        }
    }

    public static void serializeJson(BufferedWriter writer, ArrayTabulatedFunction function) throws IOException {
        writer.write("{");
        writer.newLine();
        writer.write("  \"xValues\": [");

        for (int i = 0; i < function.getCount(); i++) {
            writer.write(String.format(Locale.US, "%.6f", function.getX(i)));
            if (i < function.getCount() - 1) {
                writer.write(", ");
            }
        }

        writer.write("],");
        writer.newLine();
        writer.write("  \"yValues\": [");

        for (int i = 0; i < function.getCount(); i++) {
            writer.write(String.format(Locale.US, "%.6f", function.getY(i)));
            if (i < function.getCount() - 1) {
                writer.write(", ");
            }
        }

        writer.write("]");
        writer.newLine();
        writer.write("}");
        writer.newLine();
        writer.flush();
    }

    public static ArrayTabulatedFunction deserializeJson(BufferedReader reader) throws IOException {
        StringBuilder jsonBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        String json = jsonBuilder.toString();

        // Простой парсинг JSON
        String xData = json.split("\"xValues\":\\s*\\[")[1].split("\\]")[0];
        String yData = json.split("\"yValues\":\\s*\\[")[1].split("\\]")[0];

        String[] xParts = xData.split(",");
        String[] yParts = yData.split(",");

        int count = xParts.length;
        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) {
            xValues[i] = Double.parseDouble(xParts[i].trim());
            yValues[i] = Double.parseDouble(yParts[i].trim());
        }

        return new ArrayTabulatedFunction(xValues, yValues);
    }
}
