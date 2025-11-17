package ru.ssau.tk.enjoyers.ooplabs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcFunctionDao;
import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.PointDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/functions")
public class FunctionServlet extends HttpServlet {

    private final JdbcFunctionDao functionDao = new JdbcFunctionDao();

    public void init() throws ServletException { }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = Long.parseLong(request.getParameter("userId"));
        List<FunctionDto> functions = functionDao.findByUserId(userId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = response.getWriter();
        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        for (FunctionDto function : functions)
            printWriter.print(new JSONObject(objectMapper.writeValueAsString(function)));
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
        FunctionDto function = mapper.readValue(jsonString, FunctionDto.class);

        // сохраняем функцию
        Long savedFunctionId = functionDao.save(function);
        Optional<FunctionDto> savedFunction = functionDao.findById(savedFunctionId);

        // возвращаем функцию
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = response.getWriter();
        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        if (savedFunction.isPresent())
            printWriter.print(new JSONObject(objectMapper.writeValueAsString(savedFunction.get())));
        printWriter.close();
    }
}
