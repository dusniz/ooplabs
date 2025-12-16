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
import ru.ssau.tk.enjoyers.ooplabs.entity.Function;
import ru.ssau.tk.enjoyers.ooplabs.functions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@WebServlet("/functions/*")
public class FunctionServlet extends HttpServlet {

    private final JdbcFunctionDao functionDao = new JdbcFunctionDao();

    public void init() throws ServletException { }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Function> functions = List.of();

        String pathInfo = request.getPathInfo();
        String[] pathVariables = pathInfo.substring(1).split("/");

        System.out.println(pathVariables[0]);
        switch (pathVariables.length) {
            case 1:
                if (!Objects.equals(pathVariables[0], "")) {
                    Long id = Long.parseLong(pathVariables[0]);
                    Optional<Function> function = functionDao.findById(id);
                    if (function.isPresent()) {
                        functions = List.of(function.get());
                        break;
                    }
                } else {
                    functions = functionDao.findAll();
                    break;
                }
            case 2:
                if (pathVariables[0].equals("userId")) {
                    Long userId = Long.parseLong(pathVariables[1]);
                    functions = functionDao.findByUserId(userId);
                    break;
                }
            case 3:
                if (pathVariables[1].equals("evaluate")) {
                    Long id = Long.parseLong(pathVariables[0]);
                    Optional<Function> function = functionDao.findById(id);

                    if (function.isPresent()) {
                        String xParam = request.getParameter("x");
                        if (xParam == null) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            return;
                        }

                        double x = Double.parseDouble(xParam);
                        double result = functionClassApply(function.get(), x);

                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        PrintWriter printWriter = response.getWriter();
                        JSONObject responseJson = new JSONObject();
                        responseJson.put("functionId", id);
                        responseJson.put("x", x);
                        responseJson.put("result", result);
                        printWriter.print(responseJson.toString());
                        printWriter.close();
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                    return;
                }
                break;
            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = response.getWriter();
        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        for (Function function : functions)
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
        Function function = mapper.readValue(jsonString, Function.class);

        // сохраняем функцию
        Long savedFunctionId = null;
        try {
            savedFunctionId = functionDao.save(function);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        Optional<Function> savedFunction = functionDao.findById(savedFunctionId);

        // возвращаем функцию
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = response.getWriter();
        ObjectWriter objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        if (savedFunction.isPresent())
            printWriter.print(new JSONObject(objectMapper.writeValueAsString(savedFunction.get())));
        printWriter.close();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // парсим JSON из запроса
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null)
                jsonBuilder.append(line);
        }

        String jsonString = jsonBuilder.toString();
        ObjectMapper mapper = new ObjectMapper();
        Function function = mapper.readValue(jsonString, Function.class);

        try {
            functionDao.update(function);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String[] pathVariables = pathInfo.substring(1).split("/");

        if (pathVariables.length == 1) {
            functionDao.delete(Long.parseLong(pathVariables[0]));
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    protected double functionClassApply(Function function, double x) {
        String functionClass = function.getFunctionClass();
        return switch (functionClass) {
            case "SqrFunction" -> {
                SqrFunction sqrFunction = new SqrFunction();
                yield sqrFunction.apply(x);
            }
            case "IdentityFunction" -> {
                IdentityFunction identityFunction = new IdentityFunction();
                yield identityFunction.apply(x);
            }
//            case "ConstantFunction" -> {
//                ConstantFunction constantFunction = new ConstantFunction();
//                yield constantFunction.apply(x);
//            }
            case "ZeroFunction" -> {
                ZeroFunction zeroFunction = new ZeroFunction();
                yield zeroFunction.apply(x);
            }
            case "UnitFunction" -> {
                UnitFunction unitFunction = new UnitFunction();
                yield unitFunction.apply(x);
            }
            case "NaturalLogarithmFunction" -> {
                NaturalLogarithmFunction naturalLogarithmFunction = new NaturalLogarithmFunction();
                yield naturalLogarithmFunction.apply(x);
            }
            case "SineFunction" -> {
                SineFunction sineFunction = new SineFunction();
                yield sineFunction.apply(x);
            }
            case "CosineFunction" -> {
                CosineFunction cosineFunction = new CosineFunction();
                yield cosineFunction.apply(x);
            }
            case "TangentFunction" -> {
                TangentFunction tangentFunction = new TangentFunction();
                yield tangentFunction.apply(x);
            }
            case "CotangentFunction" -> {
                CotangentFunction cotangentFunction = new CotangentFunction();
                yield cotangentFunction.apply(x);
            }
            default -> throw new IllegalArgumentException("No such function class found!");
        };
    }
}
