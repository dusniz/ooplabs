package ru.ssau.tk.enjoyers.ooplabs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcFunctionDao;
import ru.ssau.tk.enjoyers.ooplabs.dto.SingleFunctionOperationDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.CompositeFunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.entity.Function;
import ru.ssau.tk.enjoyers.ooplabs.entity.Point;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/v1/functions/*")
public class FunctionMathServlet extends HttpServlet {

    private final JdbcFunctionDao functionDao = new JdbcFunctionDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String[] pathVariables = pathInfo != null ? pathInfo.substring(1).split("/") : new String[0];

        if (pathVariables.length == 1) {
            String operation = pathVariables[0];

            StringBuilder jsonBuilder = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
            }

            String jsonString = jsonBuilder.toString();

            try {
                switch (operation) {
                    case "differentiate":
                        handleDifferentiate(jsonString, response);
                        break;
                    case "integrate":
                        handleIntegrate(jsonString, response);
                        break;
                    case "composite":
                        handleComposite(jsonString, response);
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Unknown operation\"}");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String[] pathVariables = pathInfo != null ? pathInfo.substring(1).split("/") : new String[0];

        if (pathVariables.length == 2 && pathVariables[0].equals("math") && pathVariables[1].equals("list")) {
            // Возвращаем список доступных математических операций
            Map<String, Object> mathOperations = new HashMap<>();
            mathOperations.put("operations", Arrays.asList(
                    "add", "subtract", "multiply", "divide",
                    "differentiate", "integrate", "composite"
            ));
            mathOperations.put("factoryTypes", Arrays.asList(
                    "TabulatedFunction", "MathFunction", "CompositeFunction"
            ));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), mathOperations);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleDifferentiate(String jsonString, HttpServletResponse response) throws IOException {
        SingleFunctionOperationDto dto = objectMapper.readValue(jsonString, SingleFunctionOperationDto.class);

        Optional<Function> function = functionDao.findById(dto.getFunctionId());

        if (function.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Function not found\"}");
            return;
        }

        List<Point> points = functionDao.findPointsByFunctionId(dto.getFunctionId());

        if (points.size() < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Function must have at least 2 points for differentiation\"}");
            return;
        }

        // Создаем новую функцию для производной
        Function derivativeFunction = new Function(
                dto.getUserId(),
                dto.getResultName() != null ? dto.getResultName() : "Derivative of " + function.get().getName(),
                function.get().getType(),
                dto.getDescription() != null ? dto.getDescription() : "Derivative function",
                points.size() - 1,
                "TabulatedFunction"
        );

        Long newFunctionId = functionDao.save(derivativeFunction);

        // Вычисляем производную (разностный метод)
        List<Point> derivativePoints = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);

            double derivative = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
            double midX = (p1.getX() + p2.getX()) / 2;

            Point derivativePoint = new Point(
                    newFunctionId,
                    midX,
                    derivative,
                    i
            );
            derivativePoints.add(derivativePoint);
        }

        functionDao.savePoints(newFunctionId, derivativePoints);

        Optional<Function> savedFunction = functionDao.findById(newFunctionId);
        if (savedFunction.isPresent()) {
            Map<String, Object> result = new HashMap<>();
            result.put("function", savedFunction.get());
            result.put("points", derivativePoints);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), result);
        }
    }

    private void handleIntegrate(String jsonString, HttpServletResponse response) throws IOException {
        SingleFunctionOperationDto dto = objectMapper.readValue(jsonString, SingleFunctionOperationDto.class);

        Optional<Function> function = functionDao.findById(dto.getFunctionId());

        if (function.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Function not found\"}");
            return;
        }

        List<Point> points = functionDao.findPointsByFunctionId(dto.getFunctionId());

        if (points.size() < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Function must have at least 2 points for integration\"}");
            return;
        }

        // Вычисляем интеграл (метод трапеций)
        double integralValue = 0.0;
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);

            double trapezoidArea = (p1.getY() + p2.getY()) * (p2.getX() - p1.getX()) / 2;
            integralValue += trapezoidArea;
        }

        // Создаем функцию интеграла (кумулятивная сумма)
        Function integralFunction = new Function(
                dto.getUserId(),
                dto.getResultName() != null ? dto.getResultName() : "Integral of " + function.get().getName(),
                function.get().getType(),
                dto.getDescription() != null ? dto.getDescription() : "Integral function",
                points.size(),
                "TabulatedFunction"
        );

        Long newFunctionId = functionDao.save(integralFunction);

        // Вычисляем значения интегральной функции
        List<Point> integralPoints = new ArrayList<>();
        double cumulativeSum = 0.0;

        integralPoints.add(new Point(newFunctionId, points.get(0).getX(), 0.0, 0));

        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);

            double trapezoidArea = (p1.getY() + p2.getY()) * (p2.getX() - p1.getX()) / 2;
            cumulativeSum += trapezoidArea;

            Point integralPoint = new Point(
                    newFunctionId,
                    p2.getX(),
                    cumulativeSum,
                    i
            );
            integralPoints.add(integralPoint);
        }

        functionDao.savePoints(newFunctionId, integralPoints);

        Map<String, Object> result = new HashMap<>();
        result.put("function", functionDao.findById(newFunctionId).get());
        result.put("points", integralPoints);
        result.put("totalIntegralValue", integralValue);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), result);
    }

    private void handleComposite(String jsonString, HttpServletResponse response) throws IOException {
        CompositeFunctionDto dto = objectMapper.readValue(jsonString, CompositeFunctionDto.class);

        Optional<Function> outerFunction = functionDao.findById(dto.getOuterFunctionId());
        Optional<Function> innerFunction = functionDao.findById(dto.getInnerFunctionId());

        if (outerFunction.isEmpty() || innerFunction.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Function not found\"}");
            return;
        }

        List<Point> innerPoints = functionDao.findPointsByFunctionId(dto.getInnerFunctionId());
        List<Point> outerPoints = functionDao.findPointsByFunctionId(dto.getOuterFunctionId());

        // Для композиции f(g(x)) берем x-координаты внутренней функции
        // и вычисляем g(x), затем f(g(x))
        Function compositeFunction = new Function(
                dto.getUserId(),
                dto.getResultName() != null ? dto.getResultName() : outerFunction.get().getName() + "∘" + innerFunction.get().getName(),
                outerFunction.get().getType(),
                dto.getDescription() != null ? dto.getDescription() : "Composite function",
                innerPoints.size(),
                "CompositeFunction"
        );

        Long newFunctionId = functionDao.save(compositeFunction);

        List<Point> compositePoints = new ArrayList<>();

        for (int i = 0; i < innerPoints.size(); i++) {
            Point innerPoint = innerPoints.get(i);

            // Находим ближайшую точку во внешней функции для значения g(x)
            double innerValue = innerPoint.getY();
            Point closestOuterPoint = findClosestPoint(outerPoints, innerValue);

            // Вычисляем f(g(x))
            double compositeValue = closestOuterPoint.getY();

            Point compositePoint = new Point(
                    newFunctionId,
                    innerPoint.getX(), // x-координата из внутренней функции
                    compositeValue,
                    i
            );
            compositePoints.add(compositePoint);
        }

        functionDao.savePoints(newFunctionId, compositePoints);

        Optional<Function> savedFunction = functionDao.findById(newFunctionId);
        if (savedFunction.isPresent()) {
            Map<String, Object> result = new HashMap<>();
            result.put("function", savedFunction.get());
            result.put("points", compositePoints);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), result);
        }
    }

    private Point findClosestPoint(List<Point> points, double targetX) {
        Point closest = points.get(0);
        double minDistance = Math.abs(closest.getX() - targetX);

        for (Point point : points) {
            double distance = Math.abs(point.getX() - targetX);
            if (distance < minDistance) {
                minDistance = distance;
                closest = point;
            }
        }

        return closest;
    }
}
