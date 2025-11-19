package ru.ssau.tk.enjoyers.ooplabs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcUserDao;
import ru.ssau.tk.enjoyers.ooplabs.entity.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {

    private final JdbcUserDao userDao = new JdbcUserDao();

    public void init() throws ServletException { }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<User> users = List.of();

        String pathInfo = request.getPathInfo();
        String[] pathVariables = pathInfo.substring(1).split("/");

        if (pathVariables.length == 1) {
                Long id = Long.parseLong(pathVariables[0]);
                Optional<User> user = userDao.findById(id);
                if (user.isPresent())
                    users = List.of(user.get());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = response.getWriter();
        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        for (User user : users)
            printWriter.print(new JSONObject(objectMapper.writeValueAsString(user)));
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
        User user = mapper.readValue(jsonString, User.class);

        // сохраняем пользователя
        Long savedUserId = userDao.save(user);
        Optional<User> savedUser = userDao.findById(savedUserId);

        // возвращаем пользователя
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = response.getWriter();
        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        if (savedUser.isPresent())
            printWriter.print(new JSONObject(objectMapper.writeValueAsString(savedUser.get())));
        printWriter.close();
    }
}
