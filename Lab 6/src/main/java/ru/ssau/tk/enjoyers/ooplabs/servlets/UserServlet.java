package ru.ssau.tk.enjoyers.ooplabs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcUserDao;
import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private final JdbcUserDao userDao = new JdbcUserDao();

    public void init() throws ServletException { }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Optional<UserDto> user = userDao.findById(id);

        response.setContentType("text/html");
        PrintWriter printWriter = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();

        if (user.isPresent())
            printWriter.println(objectMapper.writeValueAsString(user.get()));

        printWriter.close();
    }
}
