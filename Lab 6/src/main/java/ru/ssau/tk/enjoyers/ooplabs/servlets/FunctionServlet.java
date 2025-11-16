package ru.ssau.tk.enjoyers.ooplabs.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcFunctionDao;
import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/function")
public class FunctionServlet extends HttpServlet {

    private final JdbcFunctionDao functionDao = new JdbcFunctionDao();

    public void init() throws ServletException { }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = Long.parseLong(request.getParameter("userId"));
        List<FunctionDto> functions = functionDao.findByUserId(userId);

        response.setContentType("text/html");
        PrintWriter printWriter = response.getWriter();
        for (FunctionDto function : functions)
            printWriter.println(function.toString());
        printWriter.close();
    }
}
