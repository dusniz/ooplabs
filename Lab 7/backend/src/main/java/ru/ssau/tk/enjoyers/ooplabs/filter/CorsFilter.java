package ru.ssau.tk.enjoyers.ooplabs.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Разрешаем конкретный origin
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // Разрешаем методы
        httpResponse.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");

        // Разрешаем заголовки
        httpResponse.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Authorization, " +
                        "X-Requested-With, Content-Length, X-CSRF-Token");

        // Для кэширования preflight запросов
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // Разрешаем заголовки для чтения браузером
        httpResponse.setHeader("Access-Control-Expose-Headers",
                "Authorization, Content-Disposition");

        // Обрабатываем OPTIONS запрос (preflight)
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}