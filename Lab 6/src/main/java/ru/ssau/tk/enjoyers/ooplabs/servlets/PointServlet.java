package ru.ssau.tk.enjoyers.ooplabs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcPointDao;
import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.PointDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/points")
public class PointServlet extends HttpServlet {

    private final JdbcPointDao pointDao = new JdbcPointDao();

    public void init() throws ServletException { }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long functionId = Long.parseLong(request.getParameter("functionId"));
        List<PointDto> points = pointDao.findByFunctionId(functionId);

        response.setContentType("text/html");
        PrintWriter printWriter = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();

        for (PointDto point : points)
            printWriter.println(objectMapper.writeValueAsString(point));

        printWriter.close();
    }
}
