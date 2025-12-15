package ru.ssau.tk.enjoyers.ooplabs;

import ru.ssau.tk.enjoyers.ooplabs.entity.Function;
import ru.ssau.tk.enjoyers.ooplabs.entity.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    private static final Random random = new Random();

    // Генерация точек для функции
    public static List<Point> generatePointsDto(Long functionId, int count, double xFrom, double xTo) {
        List<Point> points = new ArrayList<>();
        double step = (xTo - xFrom) / (count - 1);

        for (int i = 0; i < count; i++) {
            double x = xFrom + i * step;
            double y = Math.sin(x) + random.nextDouble() * 0.1; // Небольшой шум
            points.add(new Point(functionId, x, y, i));
        }

        return points;
    }

    // Генерация функций
    public static List<Function> generateFunctionsDto(Long userId, int count) {
        List<Function> functions = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            functions.add(new Function(
                    userId,
                    "TestFunction_" + i,
                    "TABULATED",
                    "Generated function",
                    random.nextInt(0, 100),
                    i % 2 == 0 ? "TABULATED_ARRAY" : "TABULATED_LINKED_LIST"
            ));
        }

        return functions;
    }

    // Генерация пользователей
    public static List<String> generateUsernames(int count) {
        List<String> usernames = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            usernames.add("generated_user_" + System.currentTimeMillis() + "_" + i);
        }
        return usernames;
    }
}