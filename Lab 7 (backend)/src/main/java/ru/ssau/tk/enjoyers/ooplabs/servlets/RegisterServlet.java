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
import ru.ssau.tk.enjoyers.ooplabs.dto.AuthDto;
import ru.ssau.tk.enjoyers.ooplabs.entity.User;
import ru.ssau.tk.enjoyers.ooplabs.util.JWTUtil;
import ru.ssau.tk.enjoyers.ooplabs.util.PasswordUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private JdbcUserDao userDao = new JdbcUserDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void init() throws ServletException { }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null)
                jsonBuilder.append(line);
        }

        String jsonString = jsonBuilder.toString();
        ObjectMapper mapper = new ObjectMapper();
        AuthDto authDto = mapper.readValue(jsonString, AuthDto.class);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseData = new HashMap<>();

        try {
            User user = new User(
                    authDto.getUsername(),
                    PasswordUtil.hashPassword(authDto.getPassword())
            );

            // сохраняем пользователя
            Long savedUserId = userDao.save(user);
            Optional<User> savedUser = userDao.findById(savedUserId);

            if (savedUser.isPresent()) {
                user = savedUser.get();
                String token = JWTUtil.generateToken(user.getUsername(), user.getRole());

                responseData.put("success", true);
                responseData.put("message", "Registration successful");
                responseData.put("token", token);
                responseData.put("user", Map.of(
                        "username", user.getUsername(),
                        "role", user.getRole()
                ));

                response.setStatus(HttpServletResponse.SC_CREATED);
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("success", false);
            responseData.put("error", e.getMessage());

        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            responseData.put("success", false);
            responseData.put("error", e.getMessage());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("success", false);
            responseData.put("error", "Registration failed. Please try again.");
        }

        objectMapper.writeValue(response.getWriter(), responseData);
    }
}
