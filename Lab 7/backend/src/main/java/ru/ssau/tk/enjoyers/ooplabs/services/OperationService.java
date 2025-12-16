package ru.ssau.tk.enjoyers.ooplabs.services;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import ru.ssau.tk.enjoyers.ooplabs.repositories.FunctionRepository;
import ru.ssau.tk.enjoyers.ooplabs.repositories.PointRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OperationService {
    private static final Logger logger = LogManager.getLogger(OperationService.class);

    private final FunctionRepository functionRepository;
    private final PointRepository pointRepository;
    private String currentFactoryType = "TABULATED"; // Значение по умолчанию

    @Autowired
    public OperationService(FunctionRepository functionRepository,
                                 PointRepository pointRepository) {
        this.functionRepository = functionRepository;
        this.pointRepository = pointRepository;
    }

    public Function addFunctions(Long firstFunctionId, Long secondFunctionId) {
        logger.info("Adding functions {} and {}", firstFunctionId, secondFunctionId);

        Function firstFunction = functionRepository.findById(firstFunctionId)
                .orElseThrow(() -> new IllegalArgumentException("First function not found"));
        Function secondFunction = functionRepository.findById(secondFunctionId)
                .orElseThrow(() -> new IllegalArgumentException("Second function not found"));

        // Получаем точки обеих функций
        List<Point> points1 = pointRepository.findByFunctionIdOrderByIndex(firstFunctionId);
        List<Point> points2 = pointRepository.findByFunctionIdOrderByIndex(secondFunctionId);

        if (points1.size() != points2.size()) {
            throw new IllegalArgumentException("Functions must have same number of points");
        }

        // Создаем новую функцию для результата
        Function resultFunction = Function.builder()
                .userId(firstFunction.getUserId())
                .name(firstFunction.getName() + " + " + secondFunction.getName())
                .description("Sum of function " + firstFunctionId + " and " + secondFunctionId)
                .type("TABULATED")
                .pointCount(points1.size())
                .functionClass("SumFunction")
                .build();

        Function savedFunction = functionRepository.save(resultFunction);

        // Создаем точки для результата
        List<Point> resultPoints = new ArrayList<>();
        for (int i = 0; i < points1.size(); i++) {
            Point p1 = points1.get(i);
            Point p2 = points2.get(i);

            Point resultPoint = Point.builder()
                    .functionId(savedFunction.getId())
                    .x(p1.getX())
                    .y(p1.getY() + p2.getY())
                    .index(i)
                    .build();
            resultPoints.add(resultPoint);
        }

        pointRepository.saveAll(resultPoints);

        logger.info("Functions added successfully. Result function ID: {}", savedFunction.getId());
        return savedFunction;
    }

    public Function subtractFunctions(Long firstFunctionId, Long secondFunctionId) {
        logger.info("Subtracting functions {} from {}", secondFunctionId, firstFunctionId);

        Function firstFunction = functionRepository.findById(firstFunctionId)
                .orElseThrow(() -> new IllegalArgumentException("First function not found"));
        Function secondFunction = functionRepository.findById(secondFunctionId)
                .orElseThrow(() -> new IllegalArgumentException("Second function not found"));

        List<Point> points1 = pointRepository.findByFunctionIdOrderByIndex(firstFunctionId);
        List<Point> points2 = pointRepository.findByFunctionIdOrderByIndex(secondFunctionId);

        if (points1.size() != points2.size()) {
            throw new IllegalArgumentException("Functions must have same number of points");
        }

        Function resultFunction = Function.builder()
                .userId(firstFunction.getUserId())
                .name(firstFunction.getName() + " - " + secondFunction.getName())
                .description("Difference of function " + firstFunctionId + " and " + secondFunctionId)
                .type("TABULATED")
                .pointCount(points1.size())
                .functionClass("DifferenceFunction")
                .build();

        Function savedFunction = functionRepository.save(resultFunction);

        List<Point> resultPoints = new ArrayList<>();
        for (int i = 0; i < points1.size(); i++) {
            Point p1 = points1.get(i);
            Point p2 = points2.get(i);

            Point resultPoint = Point.builder()
                    .functionId(savedFunction.getId())
                    .x(p1.getX())
                    .y(p1.getY() - p2.getY())
                    .index(i)
                    .build();
            resultPoints.add(resultPoint);
        }

        pointRepository.saveAll(resultPoints);

        logger.info("Functions subtracted successfully. Result function ID: {}", savedFunction.getId());
        return savedFunction;
    }

    public Function multiplyFunctions(Long firstFunctionId, Long secondFunctionId) {
        logger.info("Multiplying functions {} and {}", firstFunctionId, secondFunctionId);

        Function firstFunction = functionRepository.findById(firstFunctionId)
                .orElseThrow(() -> new IllegalArgumentException("First function not found"));
        Function secondFunction = functionRepository.findById(secondFunctionId)
                .orElseThrow(() -> new IllegalArgumentException("Second function not found"));

        List<Point> points1 = pointRepository.findByFunctionIdOrderByIndex(firstFunctionId);
        List<Point> points2 = pointRepository.findByFunctionIdOrderByIndex(secondFunctionId);

        if (points1.size() != points2.size()) {
            throw new IllegalArgumentException("Functions must have same number of points");
        }

        Function resultFunction = Function.builder()
                .userId(firstFunction.getUserId())
                .name(firstFunction.getName() + " * " + secondFunction.getName())
                .description("Product of function " + firstFunctionId + " and " + secondFunctionId)
                .type("TABULATED")
                .pointCount(points1.size())
                .functionClass("ProductFunction")
                .build();

        Function savedFunction = functionRepository.save(resultFunction);

        List<Point> resultPoints = new ArrayList<>();
        for (int i = 0; i < points1.size(); i++) {
            Point p1 = points1.get(i);
            Point p2 = points2.get(i);

            Point resultPoint = Point.builder()
                    .functionId(savedFunction.getId())
                    .x(p1.getX())
                    .y(p1.getY() * p2.getY())
                    .index(i)
                    .build();
            resultPoints.add(resultPoint);
        }

        pointRepository.saveAll(resultPoints);

        logger.info("Functions multiplied successfully. Result function ID: {}", savedFunction.getId());
        return savedFunction;
    }

    public Function divideFunctions(Long firstFunctionId, Long secondFunctionId) {
        logger.info("Dividing function {} by {}", firstFunctionId, secondFunctionId);

        Function firstFunction = functionRepository.findById(firstFunctionId)
                .orElseThrow(() -> new IllegalArgumentException("First function not found"));
        Function secondFunction = functionRepository.findById(secondFunctionId)
                .orElseThrow(() -> new IllegalArgumentException("Second function not found"));

        List<Point> points1 = pointRepository.findByFunctionIdOrderByIndex(firstFunctionId);
        List<Point> points2 = pointRepository.findByFunctionIdOrderByIndex(secondFunctionId);

        if (points1.size() != points2.size()) {
            throw new IllegalArgumentException("Functions must have same number of points");
        }

        // Проверка деления на ноль
        for (Point p2 : points2) {
            if (Math.abs(p2.getY()) < 1e-10) {
                throw new IllegalArgumentException("Division by zero detected at x = " + p2.getX());
            }
        }

        Function resultFunction = Function.builder()
                .userId(firstFunction.getUserId())
                .name(firstFunction.getName() + " / " + secondFunction.getName())
                .description("Quotient of function " + firstFunctionId + " and " + secondFunctionId)
                .type("TABULATED")
                .pointCount(points1.size())
                .functionClass("QuotientFunction")
                .build();

        Function savedFunction = functionRepository.save(resultFunction);

        List<Point> resultPoints = new ArrayList<>();
        for (int i = 0; i < points1.size(); i++) {
            Point p1 = points1.get(i);
            Point p2 = points2.get(i);

            Point resultPoint = Point.builder()
                    .functionId(savedFunction.getId())
                    .x(p1.getX())
                    .y(p1.getY() / p2.getY())
                    .index(i)
                    .build();
            resultPoints.add(resultPoint);
        }

        pointRepository.saveAll(resultPoints);

        logger.info("Functions divided successfully. Result function ID: {}", savedFunction.getId());
        return savedFunction;
    }

    public Function differentiateFunction(Long functionId, String variable) {
        logger.info("Differentiating function {} with respect to {}", functionId, variable);

        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Function not found"));


        logger.info("GET запрос на получение всех точек фукнции с ID: {}", functionId);

        List<Point> points;
        if (function.getType().equals("TABULATED")) {
            points = pointRepository.findByFunctionIdOrderByIndex(functionId);
        } else {
            ArrayList<Point> temp = new ArrayList<Point>();
            for (int x = -10; x <= 10; x++) {
                temp.add(new Point(null, functionId, (double) x, functionEvaluate(function, x), x + 10));
            }
            points = temp;
        }

        if (points.size() < 2) {
            throw new IllegalArgumentException("Function must have at least 2 points for differentiation");
        }

        Function resultFunction = Function.builder()
                .userId(function.getUserId())
                .name("d/d" + variable + "(" + function.getName() + ")")
                .description("Derivative of function " + functionId)
                .type("TABULATED")
                .pointCount(points.size() - 1)
                .functionClass("DerivativeFunction")
                .build();

        Function savedFunction = functionRepository.save(resultFunction);

        List<Point> resultPoints = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);

            double dx = p2.getX() - p1.getX();
            double dy = p2.getY() - p1.getY();
            double derivative = dy / dx;

            Point resultPoint = Point.builder()
                    .functionId(savedFunction.getId())
                    .x((p1.getX() + p2.getX()) / 2) // Средняя точка
                    .y(derivative)
                    .index(i)
                    .build();
            resultPoints.add(resultPoint);
        }

        pointRepository.saveAll(resultPoints);

        logger.info("Function differentiated successfully. Result function ID: {}", savedFunction.getId());
        return savedFunction;
    }

    public Function integrateFunction(Long functionId, String variable, Double lowerLimit, Double upperLimit) {
        logger.info("Integrating function {} from {} to {} with respect to {}",
                functionId, lowerLimit, upperLimit, variable);

        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Function not found"));

        List<Point> points;
        if (function.getType().equals("TABULATED")) {
            points = pointRepository.findByFunctionIdOrderByIndex(functionId);
        } else {
            ArrayList<Point> temp = new ArrayList<Point>();
            for (int x = -10; x <= 10; x++) {
                temp.add(new Point(null, functionId, (double) x, functionEvaluate(function, x), x + 10));
            }
            points = temp;
        }

        if (points.size() < 2) {
            throw new IllegalArgumentException("Function must have at least 2 points for integration");
        }

        // Численное интегрирование методом трапеций
        double integral = 0.0;
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);

            double base = p2.getX() - p1.getX();
            double averageHeight = (p1.getY() + p2.getY()) / 2;
            integral += base * averageHeight;
        }

        // Создаем функцию результата (в данном случае константная функция)
        Function resultFunction = Function.builder()
                .userId(function.getUserId())
                .name("∫" + function.getName() + " d" + variable)
                .description("Integral of function " + functionId + " from " + lowerLimit + " to " + upperLimit)
                .type("MATH")
                .pointCount(1)
                .functionClass("IntegralFunction")
                .build();

        Function savedFunction = functionRepository.save(resultFunction);

        // Создаем точку с результатом интегрирования
        Point resultPoint = Point.builder()
                .functionId(savedFunction.getId())
                .x(0.0) // Средняя точка интервала
                .y(integral)
                .index(0)
                .build();

        pointRepository.save(resultPoint);

        logger.info("Function integrated successfully. Result: {}, function ID: {}", integral, savedFunction.getId());
        return savedFunction;
    }

    public Function createCompositeFunction(Long outerFunctionId, Long innerFunctionId) {
        logger.info("Creating composite function: f(g(x)) where f={}, g={}", outerFunctionId, innerFunctionId);

        Function outerFunction = functionRepository.findById(outerFunctionId)
                .orElseThrow(() -> new IllegalArgumentException("Outer function not found"));
        Function innerFunction = functionRepository.findById(innerFunctionId)
                .orElseThrow(() -> new IllegalArgumentException("Inner function not found"));

        List<Point> innerPoints = pointRepository.findByFunctionIdOrderByIndex(innerFunctionId);
        List<Point> outerPoints = pointRepository.findByFunctionIdOrderByIndex(outerFunctionId);

        Function resultFunction = Function.builder()
                .userId(outerFunction.getUserId())
                .name(outerFunction.getName() + "(" + innerFunction.getName() + "(x))")
                .description("Composite of function " + outerFunctionId + " and " + innerFunctionId)
                .type("TABULATED")
                .pointCount(innerPoints.size())
                .functionClass("CompositeFunction")
                .build();

        Function savedFunction = functionRepository.save(resultFunction);

        // Для композиции: f(g(x)) - применяем внешнюю функцию к результатам внутренней
        List<Point> resultPoints = new ArrayList<>();
        for (int i = 0; i < innerPoints.size(); i++) {
            Point innerPoint = innerPoints.get(i);

            // Находим ближайшую точку во внешней функции
            double yResult = applyFunction(outerPoints, innerPoint.getY());

            Point resultPoint = Point.builder()
                    .functionId(savedFunction.getId())
                    .x(innerPoint.getX())
                    .y(yResult)
                    .index(i)
                    .build();
            resultPoints.add(resultPoint);
        }

        pointRepository.saveAll(resultPoints);

        logger.info("Composite function created successfully. Result function ID: {}", savedFunction.getId());
        return savedFunction;
    }

    private double applyFunction(List<Point> functionPoints, double xValue) {
        // Линейная интерполяция для применения функции к значению
        for (int i = 0; i < functionPoints.size() - 1; i++) {
            Point p1 = functionPoints.get(i);
            Point p2 = functionPoints.get(i + 1);

            if (xValue >= p1.getX() && xValue <= p2.getX()) {
                // Линейная интерполяция
                double t = (xValue - p1.getX()) / (p2.getX() - p1.getX());
                return p1.getY() + t * (p2.getY() - p1.getY());
            }
        }

        // Если значение вне диапазона, возвращаем значение ближайшей точки
        if (xValue < functionPoints.get(0).getX()) {
            return functionPoints.get(0).getY();
        } else {
            return functionPoints.get(functionPoints.size() - 1).getY();
        }
    }

    public List<String> getAvailableMathFunctions() {
        logger.info("Retrieving list of available mathematical functions");

        List<String> functions = new ArrayList<>();
        functions.add("Линейная: y = x");
        functions.add("Квадратичная: y = x^2");
        functions.add("Нулевая: y = 0");
        functions.add("Единичная: y = 1");
        functions.add("Натуральный логарифм: y = ln(x)");
        functions.add("Синусоида y = sin(x)");
        functions.add("Косинусоида: y = cos(x)");
        functions.add("Тангенс: y = tan(x)");
        functions.add("Котангенс: y = cot(x)");

        return functions;
    }

    public Function createFunctionFromMathExpression(Long userId, String name, String description, String functionClass) {
        logger.info("Creating function from math class: {}", functionClass);

        Function function = Function.builder()
                .userId(userId)
                .name(name)
                .description(description)
                .type("MATH")
                .pointCount(0)
                .functionClass(functionClass)
                .build();

        Function savedFunction = functionRepository.save(function);
        functionRepository.save(function);

        logger.info("Function created from math expression. ID: {}", savedFunction.getId());
        return savedFunction;
    }

    public Function createFunctionFromPoints(Long userId, String name, List<Point> points) {
        logger.info("Creating function from {} points for user {}", points.size(), userId);

        Function function = Function.builder()
                .userId(userId)
                .name(name)
                .description("Tabulated function with " + points.size() + " points")
                .type("TABULATED")
                .pointCount(points.size())
                .functionClass("TabulatedFunction")
                .build();

        Function savedFunction = functionRepository.save(function);

        // Привязываем точки к функции
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            point.setFunctionId(savedFunction.getId());
            point.setIndex(i);
            pointRepository.save(point);
        }

        logger.info("Function created from points. ID: {}", savedFunction.getId());
        return savedFunction;
    }

    // Методы для управления типом фабрики
    public String getFactoryType() {
        return currentFactoryType;
    }

    public void setFactoryType(String factoryType) {
        if (!factoryType.equals("ARRAY") && !factoryType.equals("LIST")) {
            throw new IllegalArgumentException("Invalid factory type. Must be 'ARRAY' or 'LIST'");
        }
        this.currentFactoryType = factoryType;
        logger.info("Factory type changed to: {}", factoryType);
    }

    public double functionEvaluate(Function function, double x) {
        String functionClass = function.getFunctionClass();
        return switch (functionClass) {
            case "SqrFunction" -> {
                SqrFunction sqrFunction = new SqrFunction();
                yield sqrFunction.apply(x);
            }
            case "IdentityFunction" -> {
                IdentityFunction identityFunction = new IdentityFunction();
                yield identityFunction.apply(x);
            }
//            case "ConstantFunction" -> {
//                ConstantFunction constantFunction = new ConstantFunction();
//                yield constantFunction.apply(x);
//            }
            case "ZeroFunction" -> {
                ZeroFunction zeroFunction = new ZeroFunction();
                yield zeroFunction.apply(x);
            }
            case "UnitFunction" -> {
                UnitFunction unitFunction = new UnitFunction();
                yield unitFunction.apply(x);
            }
            case "NaturalLogarithmFunction" -> {
                NaturalLogarithmFunction naturalLogarithmFunction = new NaturalLogarithmFunction();
                yield naturalLogarithmFunction.apply(x);
            }
            case "SineFunction" -> {
                SineFunction sineFunction = new SineFunction();
                yield sineFunction.apply(x);
            }
            case "CosineFunction" -> {
                CosineFunction cosineFunction = new CosineFunction();
                yield cosineFunction.apply(x);
            }
            case "TangentFunction" -> {
                TangentFunction tangentFunction = new TangentFunction();
                yield tangentFunction.apply(x);
            }
            case "CotangentFunction" -> {
                CotangentFunction cotangentFunction = new CotangentFunction();
                yield cotangentFunction.apply(x);
            }
            default -> throw new IllegalArgumentException("No such function class found!");
        };
    }
}
