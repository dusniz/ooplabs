package ru.ssau.tk.enjoyers.ooplabs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcFunctionDao;
import ru.ssau.tk.enjoyers.ooplabs.dto.CreateFunctionFromMathDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.CreateFunctionFromPointsDto;
import ru.ssau.tk.enjoyers.ooplabs.entity.Function;
import ru.ssau.tk.enjoyers.ooplabs.entity.Point;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/v1/functions/create/*")
public class CreateFunctionServlet extends HttpServlet {

    private final JdbcFunctionDao functionDao = new JdbcFunctionDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String[] pathVariables = pathInfo != null ? pathInfo.substring(1).split("/") : new String[0];

        if (pathVariables.length == 1) {
            String method = pathVariables[0];

            StringBuilder jsonBuilder = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
            }

            String jsonString = jsonBuilder.toString();

            try {
                switch (method) {
//                    case "from-math":
//                        createFromMath(jsonString, response);
//                        break;
                    case "from-points":
                        createFromPoints(jsonString, response);
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Unknown creation method\"}");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

//    private void createFromMath(String jsonString, HttpServletResponse response) throws IOException {
//        CreateFunctionFromMathDto dto = objectMapper.readValue(jsonString, CreateFunctionFromMathDto.class);
//
//        if (dto.getStartX() >= dto.getEndX()) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write("{\"error\": \"startX must be less than endX\"}");
//            return;
//        }
//
//        if (dto.getPointCount() <= 1) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write("{\"error\": \"pointCount must be greater than 1\"}");
//            return;
//        }
//
//        // Создаем функцию
//        Function function = new Function(
//                dto.getUserId(),
//                dto.getName(),
//                "MathFunction",
//                dto.getDescription() != null ? dto.getDescription() : "Created from math expression: " + dto.getMathExpression(),
//                dto.getPointCount(),
//                "MathFunction"
//        );
//
//        Long functionId = functionDao.save(function);
//
//        // Генерируем точки на основе математического выражения
//        List<Point> points = new ArrayList<>();
//        double step = (dto.getEndX() - dto.getStartX()) / (dto.getPointCount() - 1);
//
//        for (int i = 0; i < dto.getPointCount(); i++) {
//            double x = dto.getStartX() + i * step;
//
//            // Здесь должна быть реальная логика парсинга математического выражения
//            // Для примера: поддерживаем простые выражения типа "x^2", "sin(x)", "2*x+1"
//            double y = evaluateMathExpression(dto.getMathExpression(), x);
//
//            Point point = new Point(
//                    functionId,
//                    x,
//                    y,
//                    i
//            );
//            points.add(point);
//        }
//
//        functionDao.savePoints(functionId, points);
//
//        Optional<Function> savedFunction = functionDao.findById(functionId);
//        if (savedFunction.isPresent()) {
//            Map<String, Object> result = new HashMap<>();
//            result.put("function", savedFunction.get());
//            result.put("points", points);
//
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            objectMapper.writeValue(response.getWriter(), result);
//        }
//    }

    private void createFromPoints(String jsonString, HttpServletResponse response) throws IOException {
        CreateFunctionFromPointsDto dto = objectMapper.readValue(jsonString, CreateFunctionFromPointsDto.class);

        if (dto.getPoints() == null || dto.getPoints().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Points list cannot be empty\"}");
            return;
        }

        // Проверяем, что индексы уникальны и последовательны
        Set<Integer> indices = new HashSet<>();
        for (Point point : dto.getPoints()) {
            if (indices.contains(point.getIndex())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Duplicate point index: " + point.getIndex() + "\"}");
                return;
            }
            indices.add(point.getIndex());
        }

        // Создаем функцию
        Function function = new Function(
                dto.getUserId(),
                dto.getName(),
                dto.getType() != null ? dto.getType() : "TabulatedFunction",
                dto.getDescription() != null ? dto.getDescription() : "Created from points",
                dto.getPoints().size(),
                dto.getFunctionClass() != null ? dto.getFunctionClass() : "TabulatedFunction"
        );

        Long functionId = functionDao.save(function);

        // Сохраняем точки с обновленным functionId
        List<Point> pointsToSave = new ArrayList<>();
        for (Point point : dto.getPoints()) {
            Point newPoint = new Point(
                    functionId,
                    point.getX(),
                    point.getY(),
                    point.getIndex()
            );
            pointsToSave.add(newPoint);
        }

        functionDao.savePoints(functionId, pointsToSave);

        Optional<Function> savedFunction = functionDao.findById(functionId);
        if (savedFunction.isPresent()) {
            Map<String, Object> result = new HashMap<>();
            result.put("function", savedFunction.get());
            result.put("points", pointsToSave);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), result);
        }
    }
}