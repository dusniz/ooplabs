package ru.ssau.tk.enjoyers.ooplabs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcPointDao;
import ru.ssau.tk.enjoyers.ooplabs.entity.Point;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/points/*")
public class PointServlet extends HttpServlet {

    private final JdbcPointDao pointDao = new JdbcPointDao();

    public void init() throws ServletException { }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Point> points = List.of();

        String pathInfo = request.getPathInfo();
        String[] pathVariables = pathInfo.substring(1).split("/");

        switch (pathVariables.length) {
            case 1:
                Long id = Long.parseLong(pathVariables[0]);
                Optional<Point> point = pointDao.findById(id);
                if (point.isPresent()) {
                    points = List.of(point.get());
                    break;
                }
            case 2:
                if (pathVariables[0].equals("functionId")) {
                    Long functionId = Long.parseLong(pathVariables[1]);
                    points = pointDao.findByFunctionId(functionId);
                    break;
                }
            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = response.getWriter();
        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        for (Point point : points)
            printWriter.print(new JSONObject(objectMapper.writeValueAsString(point)));
        printWriter.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // парсим JSON из запроса
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null)
                jsonBuilder.append(line);
        }

        String jsonString = jsonBuilder.toString();
        ObjectMapper mapper = new ObjectMapper();
        Point point = mapper.readValue(jsonString, Point.class);

        // сохраняем точку
        Long savedPointId = pointDao.save(point);
        Optional<Point> savedPoint = pointDao.findById(savedPointId);

        // возвращаем точку
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = response.getWriter();
        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        if (savedPoint.isPresent())
            printWriter.print(new JSONObject(objectMapper.writeValueAsString(savedPoint.get())));
        printWriter.close();
    }
}
