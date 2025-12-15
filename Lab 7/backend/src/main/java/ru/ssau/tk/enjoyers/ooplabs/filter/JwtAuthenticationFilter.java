package ru.ssau.tk.enjoyers.ooplabs.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk.enjoyers.ooplabs.util.JWTUtil;

import java.io.IOException;

@WebFilter("/*")
public class JwtAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String path = httpRequest.getRequestURI();
        if (path.contains("/auth") || path.contains("/register")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(httpResponse, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        try {
            String username = JWTUtil.getUsernameFromToken(token);
            String role = JWTUtil.getRoleFromToken(token);

            httpRequest.setAttribute("username", username);
            httpRequest.setAttribute("role", role);

            if (!hasAccess(path, role)) {
                sendError(httpResponse, "Insufficient p" +
                        "rmissions");
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);

        } catch (Exception e) {
            sendError(httpResponse, "Invalid token: " + e.getMessage());
        }
    }

    private boolean hasAccess(String path, String role) {
        if (path.contains("/admin") && !"ADMIN".equals(role))
            return false;
        return true;
    }

    private void sendError(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}