package ru.ssau.tk.enjoyers.ooplabs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcFunctionDao;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcPointDao;
import ru.ssau.tk.enjoyers.ooplabs.dto.MathOperationDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.SingleFunctionOperationDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.CompositeFunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.entity.Function;
import ru.ssau.tk.enjoyers.ooplabs.entity.Point;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/api/v1/functions/operations/*")
public class OperationsServlet extends HttpServlet {

    private final JdbcFunctionDao functionDao = new JdbcFunctionDao();
    private final JdbcPointDao pointDao = new JdbcPointDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String[] pathVariables = pathInfo != null ? pathInfo.substring(1).split("/") : new String[0];

        if (pathVariables.length == 1) {
            String operation = pathVariables[0];

            // Читаем JSON из запроса
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
                    case "add":
                        handleAddOperation(jsonString, response);
                        break;
                    case "subtract":
                        handleSubtractOperation(jsonString, response);
                        break;
                    case "multiply":
                        handleMultiplyOperation(jsonString, response);
                        break;
                    case "divide":
                        handleDivideOperation(jsonString, response);
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

    private void handleAddOperation(String jsonString, HttpServletResponse response) throws IOException {
        MathOperationDto dto = objectMapper.readValue(jsonString, MathOperationDto.class);

        // Получаем функции из БД
        Optional<Function> function1 = functionDao.findById(dto.getFunctionId1());
        Optional<Function> function2 = functionDao.findById(dto.getFunctionId2());

        if (function1.isEmpty() || function2.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Function not found\"}");
            return;
        }

        // Получаем точки функций
        List<Point> points1 = functionDao.findPointsByFunctionId(dto.getFunctionId1());
        List<Point> points2 = functionDao.findPointsByFunctionId(dto.getFunctionId2());

        // Проверяем, что функции имеют одинаковое количество точек
        if (points1.size() != points2.size()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Functions must have same number of points\"}");
            return;
        }

        // Создаем новую функцию
        Function newFunction = new Function(
                dto.getUserId(),
                dto.getResultName() != null ? dto.getResultName() : "Sum Function",
                function1.get().getType(),
                dto.getDescription() != null ? dto.getDescription() : "Sum of two functions",
                points1.size(),
                "TabulatedFunction"
        );

        Long newFunctionId = functionDao.save(newFunction);

        // Вычисляем сумму точек
        List<Point> resultPoints = new ArrayList<>();
        for (int i = 0; i < points1.size(); i++) {
            Point p1 = points1.get(i);
            Point p2 = points2.get(i);

            // Проверяем, что точки имеют одинаковые координаты X
            if (!p1.getX().equals(p2.getX())) {
                functionDao.delete(newFunctionId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Points must have same x coordinates\"}");
                return;
            }

            Point newPoint = new Point(
                    newFunctionId,
                    p1.getX(),
                    p1.getY() + p2.getY(),
                    i
            );
            resultPoints.add(newPoint);
        }

        // Сохраняем точки
        functionDao.savePoints(newFunctionId, resultPoints);

        // Возвращаем результат
        Optional<Function> savedFunction = functionDao.findById(newFunctionId);
        if (savedFunction.isPresent()) {
            Map<String, Object> result = new HashMap<>();
            result.put("function", savedFunction.get());
            result.put("points", resultPoints);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), result);
        }
    }

    private void handleSubtractOperation(String jsonString, HttpServletResponse response) throws IOException {
        MathOperationDto dto = objectMapper.readValue(jsonString, MathOperationDto.class);

        Optional<Function> function1 = functionDao.findById(dto.getFunctionId1());
        Optional<Function> function2 = functionDao.findById(dto.getFunctionId2());

        if (function1.isEmpty() || function2.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Function not found\"}");
            return;
        }

        List<Point> points1 = functionDao.findPointsByFunctionId(dto.getFunctionId1());
        List<Point> points2 = functionDao.findPointsByFunctionId(dto.getFunctionId2());

        if (points1.size() != points2.size()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Functions must have same number of points\"}");
            return;
        }

        Function newFunction = new Function(
                dto.getUserId(),
                dto.getResultName() != null ? dto.getResultName() : "Difference Function",
                function1.get().getType(),
                dto.getDescription() != null ? dto.getDescription() : "Difference of two functions",
                points1.size(),
                "TabulatedFunction"
        );

        Long newFunctionId = functionDao.save(newFunction);

        List<Point> resultPoints = new ArrayList<>();
        for (int i = 0; i < points1.size(); i++) {
            Point p1 = points1.get(i);
            Point p2 = points2.get(i);

            if (!p1.getX().equals(p2.getX())) {
                functionDao.delete(newFunctionId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Points must have same x coordinates\"}");
                return;
            }

            Point newPoint = new Point(
                    newFunctionId,
                    p1.getX(),
                    p1.getY() - p2.getY(),
                    i
            );
            resultPoints.add(newPoint);
        }

        functionDao.savePoints(newFunctionId, resultPoints);

        Optional<Function> savedFunction = functionDao.findById(newFunctionId);
        if (savedFunction.isPresent()) {
            Map<String, Object> result = new HashMap<>();
            result.put("function", savedFunction.get());
            result.put("points", resultPoints);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), result);
        }
    }

    private void handleMultiplyOperation(String jsonString, HttpServletResponse response) throws IOException {
        MathOperationDto dto = objectMapper.readValue(jsonString, MathOperationDto.class);

        Optional<Function> function1 = functionDao.findById(dto.getFunctionId1());
        Optional<Function> function2 = functionDao.findById(dto.getFunctionId2());

        if (function1.isEmpty() || function2.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Function not found\"}");
            return;
        }

        List<Point> points1 = functionDao.findPointsByFunctionId(dto.getFunctionId1());
        List<Point> points2 = functionDao.findPointsByFunctionId(dto.getFunctionId2());

        if (points1.size() != points2.size()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Functions must have same number of points\"}");
            return;
        }

        Function newFunction = new Function(
                dto.getUserId(),
                dto.getResultName() != null ? dto.getResultName() : "Product Function",
                function1.get().getType(),
                dto.getDescription() != null ? dto.getDescription() : "Product of two functions",
                points1.size(),
                "TabulatedFunction"
        );

        Long newFunctionId = functionDao.save(newFunction);

        List<Point> resultPoints = new ArrayList<>();
        for (int i = 0; i < points1.size(); i++) {
            Point p1 = points1.get(i);
            Point p2 = points2.get(i);

            if (!p1.getX().equals(p2.getX())) {
                functionDao.delete(newFunctionId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Points must have same x coordinates\"}");
                return;
            }

            Point newPoint = new Point(
                    newFunctionId,
                    p1.getX(),
                    p1.getY() * p2.getY(),
                    i
            );
            resultPoints.add(newPoint);
        }

        functionDao.savePoints(newFunctionId, resultPoints);

        Optional<Function> savedFunction = functionDao.findById(newFunctionId);
        if (savedFunction.isPresent()) {
            Map<String, Object> result = new HashMap<>();
            result.put("function", savedFunction.get());
            result.put("points", resultPoints);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), result);
        }
    }

    private void handleDivideOperation(String jsonString, HttpServletResponse response) throws IOException {
        MathOperationDto dto = objectMapper.readValue(jsonString, MathOperationDto.class);

        Optional<Function> function1 = functionDao.findById(dto.getFunctionId1());
        Optional<Function> function2 = functionDao.findById(dto.getFunctionId2());

        if (function1.isEmpty() || function2.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Function not found\"}");
            return;
        }

        List<Point> points1 = functionDao.findPointsByFunctionId(dto.getFunctionId1());
        List<Point> points2 = functionDao.findPointsByFunctionId(dto.getFunctionId2());

        if (points1.size() != points2.size()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Functions must have same number of points\"}");
            return;
        }

        Function newFunction = new Function(
                dto.getUserId(),
                dto.getResultName() != null ? dto.getResultName() : "Quotient Function",
                function1.get().getType(),
                dto.getDescription() != null ? dto.getDescription() : "Quotient of two functions",
                points1.size(),
                "TabulatedFunction"
        );

        Long newFunctionId = functionDao.save(newFunction);

        List<Point> resultPoints = new ArrayList<>();
        for (int i = 0; i < points1.size(); i++) {
            Point p1 = points1.get(i);
            Point p2 = points2.get(i);

            if (!p1.getX().equals(p2.getX())) {
                functionDao.delete(newFunctionId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Points must have same x coordinates\"}");
                return;
            }

            if (p2.getY() == 0) {
                functionDao.delete(newFunctionId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Division by zero at point index " + i + "\"}");
                return;
            }

            Point newPoint = new Point(
                    newFunctionId,
                    p1.getX(),
                    p1.getY() / p2.getY(),
                    i
            );
            resultPoints.add(newPoint);
        }

        functionDao.savePoints(newFunctionId, resultPoints);

        Optional<Function> savedFunction = functionDao.findById(newFunctionId);
        if (savedFunction.isPresent()) {
            Map<String, Object> result = new HashMap<>();
            result.put("function", savedFunction.get());
            result.put("points", resultPoints);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), result);
        }
    }
}
