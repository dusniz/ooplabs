package ru.ssau.tk.enjoyers.ooplabs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcUserDao;
import ru.ssau.tk.enjoyers.ooplabs.dto.AuthDto;
import ru.ssau.tk.enjoyers.ooplabs.entity.User;
import ru.ssau.tk.enjoyers.ooplabs.util.JWTUtil;
import ru.ssau.tk.enjoyers.ooplabs.util.PasswordUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet("/auth")
public class LoginServlet extends HttpServlet {

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
            Optional<User> foundUser = userDao.findByUsername(authDto.getUsername());

            if (foundUser.isPresent() && PasswordUtil.verifyPassword(authDto.getPassword(), foundUser.get().getPasswordHash())) {
                User user = foundUser.get();
                String token = JWTUtil.generateToken(user.getUsername(), user.getRole());

                responseData.put("token", token);
                responseData.put("user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole()
                ));

                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getWriter(), responseData);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid credentials");
                objectMapper.writeValue(response.getWriter(), error);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            objectMapper.writeValue(response.getWriter(), error);
        }
    }
}
