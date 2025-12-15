package ru.ssau.tk.enjoyers.ooplabs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/v1/settings/*")
public class SettingsServlet extends HttpServlet {

    private static String currentFactoryType = "TabulatedFunction";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String[] pathVariables = pathInfo != null ? pathInfo.substring(1).split("/") : new String[0];

        if (pathVariables.length == 1 && pathVariables[0].equals("factory-type")) {
            Map<String, String> result = new HashMap<>();
            result.put("factoryType", currentFactoryType);
            result.put("availableTypes", "TabulatedFunction,MathFunction,CompositeFunction");

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), result);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String[] pathVariables = pathInfo != null ? pathInfo.substring(1).split("/") : new String[0];

        if (pathVariables.length == 1 && pathVariables[0].equals("factory-type")) {
            StringBuilder jsonBuilder = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
            }

            String jsonString = jsonBuilder.toString();
            Map<String, String> requestData = objectMapper.readValue(jsonString, Map.class);

            String newFactoryType = requestData.get("factoryType");

            if (isValidFactoryType(newFactoryType)) {
                currentFactoryType = newFactoryType;

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "Factory type updated to " + newFactoryType);
                result.put("factoryType", currentFactoryType);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), result);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid factory type. Must be one of: TabulatedFunction, MathFunction, CompositeFunction");
                objectMapper.writeValue(response.getWriter(), error);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private boolean isValidFactoryType(String factoryType) {
        return factoryType != null && (
                factoryType.equals("TabulatedFunction") ||
                        factoryType.equals("MathFunction") ||
                        factoryType.equals("CompositeFunction")
        );
    }
}
