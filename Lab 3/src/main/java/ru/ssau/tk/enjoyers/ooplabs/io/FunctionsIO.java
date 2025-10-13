package ru.ssau.tk.enjoyers.ooplabs.io;

import ru.ssau.tk.enjoyers.ooplabs.functions.Point;
import ru.ssau.tk.enjoyers.ooplabs.functions.TabulatedFunction;
import java.io.*;

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
}
