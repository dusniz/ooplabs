package ru.ssau.tk.enjoyers.ooplabs;

import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    private static final Random random = new Random();

    // Генерация точек для функции
    public static List<Point> generatePoints(Long functionId, int count, double xFrom, double xTo) {
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
    public static List<Function> generateFunctions(Long userId, int count, String type, String functionClass) {
        List<Function> functions = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            functions.add(new Function(
                    userId,
                    "TestFunction_" + i,
                    "Generated function ",
                    type,
                    random.nextInt(0, 100),
                    functionClass
            ));
        }

        return functions;
    }

    // Генерация пользователей
    public static List<User> generateUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++)
            users.add(new User("generated_user_" + i, "password", Role.USER));
        return users;
    }
}