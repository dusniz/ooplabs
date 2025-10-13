package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.ssau.tk.enjoyers.ooplabs.functions.factory.*;
import ru.ssau.tk.enjoyers.ooplabs.io.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class FunctionsIOTest {

    private static final String TEMP_DIR = "temp";

    @BeforeAll
    static void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEMP_DIR));
    }

    @AfterAll
    static void tearDown() throws IOException {
        // Очистка временной директории
        Files.walk(Paths.get(TEMP_DIR))
                .map(Path::toFile)
                .forEach(File::delete);
        Files.deleteIfExists(Paths.get(TEMP_DIR));
    }

    @Test
    void testTextFileWriteAndRead() throws IOException {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);

        // Запись
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_DIR + "/test.txt"))) {
            FunctionsIO.writeTabulatedFunction(writer, original);
        }

        // Чтение
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        try (BufferedReader reader = new BufferedReader(new FileReader(TEMP_DIR + "/test.txt"))) {
            TabulatedFunction readFunction = FunctionsIO.readTabulatedFunction(reader, factory);

            assertEquals(3, readFunction.getCount());
            assertEquals(0.0, readFunction.getX(0), 1e-12);
            assertEquals(1.0, readFunction.getX(1), 1e-12);
            assertEquals(2.0, readFunction.getX(2), 1e-12);
            assertEquals(0.0, readFunction.getY(0), 1e-12);
            assertEquals(1.0, readFunction.getY(1), 1e-12);
            assertEquals(4.0, readFunction.getY(2), 1e-12);
        }
    }

    @Test
    void testBinaryFileWriteAndRead() throws IOException {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);

        // Запись
        try (BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(TEMP_DIR + "/test.bin"))) {
            FunctionsIO.writeTabulatedFunction(outputStream, original);
        }

        // Чтение
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        try (BufferedInputStream inputStream = new BufferedInputStream(
                new FileInputStream(TEMP_DIR + "/test.bin"))) {
            TabulatedFunction readFunction = FunctionsIO.readTabulatedFunction(inputStream, factory);

            assertEquals(3, readFunction.getCount());
            assertEquals(0.0, readFunction.getX(0), 1e-12);
            assertEquals(1.0, readFunction.getX(1), 1e-12);
            assertEquals(2.0, readFunction.getX(2), 1e-12);
        }
    }

    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);

        // Сериализация
        try (BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(TEMP_DIR + "/test.ser"))) {
            FunctionsIO.serialize(outputStream, original);
        }

        // Десериализация
        try (BufferedInputStream inputStream = new BufferedInputStream(
                new FileInputStream(TEMP_DIR + "/test.ser"))) {
            TabulatedFunction deserialized = FunctionsIO.deserialize(inputStream);

            assertTrue(deserialized instanceof ArrayTabulatedFunction);
            assertEquals(3, deserialized.getCount());
            assertEquals(4.0, deserialized.apply(2.0), 1e-12);
        }
    }

    @Test
    void testXmlSerialization() throws IOException {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);

        // XML запись
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_DIR + "/test.xml"))) {
            FunctionsIO.serializeXml(writer, original);
        }

        // XML чтение
        try (BufferedReader reader = new BufferedReader(new FileReader(TEMP_DIR + "/test.xml"))) {
            ArrayTabulatedFunction xmlFunction = FunctionsIO.deserializeXml(reader);

            assertEquals(3, xmlFunction.getCount());
            assertEquals(1.0, xmlFunction.getX(1), 1e-12);
            assertEquals(4.0, xmlFunction.getY(2), 1e-12);
        }
    }

    @Test
    void testJsonSerialization() throws IOException {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);

        // JSON запись
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_DIR + "/test.json"))) {
            FunctionsIO.serializeJson(writer, original);
        }

        // JSON чтение
        try (BufferedReader reader = new BufferedReader(new FileReader(TEMP_DIR + "/test.json"))) {
            ArrayTabulatedFunction jsonFunction = FunctionsIO.deserializeJson(reader);

            assertEquals(3, jsonFunction.getCount());
            assertEquals(1.0, jsonFunction.getX(1), 1e-12);
            assertEquals(4.0, jsonFunction.getY(2), 1e-12);
        }
    }
}