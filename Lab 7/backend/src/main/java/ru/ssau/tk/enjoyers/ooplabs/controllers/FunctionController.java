package ru.ssau.tk.enjoyers.ooplabs.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.enjoyers.ooplabs.dto.*;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.exceptions.TooComplex;
import ru.ssau.tk.enjoyers.ooplabs.functions.SqrFunction;
import ru.ssau.tk.enjoyers.ooplabs.services.FunctionService;
import ru.ssau.tk.enjoyers.ooplabs.services.OperationService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/functions")
public class FunctionController {

    private static final Logger logger = LogManager.getLogger(FunctionController.class);

    @Autowired
    private FunctionService functionService;

    @Autowired
    private OperationService operationService;

    @GetMapping("/{id}")
    public ResponseEntity<Function> getFunctionById(@PathVariable("id") Long id) {
        logger.info("GET запрос на получение функции с ID: {}", id);
        try {
            return functionService.getFunction(id).map(function -> {
                logger.info("Функция с ID: {} успешно найдена", id);
                return ResponseEntity.ok(function);
            }).orElseGet(() -> {
                logger.warn("Функция с ID: {} не найдена", id);
                return ResponseEntity.notFound().build();});
        } catch (IllegalArgumentException e) {
            logger.error("function GET BAD_REQUEST ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("function GET INTERNAL_SERVER_ERROR ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/evaluate")
    public ResponseEntity<EvaluationResponse> evaluateFunctionAt(
            @PathVariable("id") Long id,
            @RequestParam(value = "x", required = true) Double x) {

        logger.info("GET запрос на вычисление функции с ID: {} в точке x={}", id, x);
        try {
            Optional<Function> function = functionService.getFunction(id);
            if (function.isEmpty()) {
                logger.warn("Функция с ID: {} не найдена", id);
                return ResponseEntity.notFound().build();
            }
            Double result = functionService.functionEvaluate(function.get(), x);
            EvaluationResponse evaluationResult = new EvaluationResponse(id, x, result);
            logger.info("Функция с ID: {} успешно вычислена в точке x={}, результат: {}",
                    id, x, result);
            return ResponseEntity.ok(evaluationResult);
        } catch (IllegalArgumentException e) {
            logger.error("function evaluate GET BAD_REQUEST ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("function evaluate GET INTERNAL_SERVER_ERROR ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("")
    public ResponseEntity<Function> createFunction(@RequestBody Function function) {
        logger.info("POST запрос на создание функции с данными: {}", function);
        try {
            Function newFunction = functionService.createFunction(function);
            logger.info("Функция успешно создана с ID: {}", newFunction.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(newFunction);
        } catch (TooComplex e) {
            logger.error("function POST BAD_REQUEST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("function POST INTERNAL_SERVER_ERROR {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/operations/add")
    public ResponseEntity<FunctionResponse> addFunctions(@RequestBody FunctionOperationRequest request) {
        logger.info("POST запрос на сложение функций {} и {}",
                request.getFirstFunctionId(), request.getSecondFunctionId());
        try {
            Function result = operationService.addFunctions(
                    request.getFirstFunctionId(),
                    request.getSecondFunctionId()
            );

            FunctionResponse response = FunctionResponse.builder()
                    .id(result.getId())
                    .userId(result.getUserId())
                    .name(result.getName())
                    .description(result.getDescription())
                    .type(result.getType())
                    .pointCount(result.getPointCount())
                    .functionClass(result.getFunctionClass())
                    .build();

            logger.info("Функции успешно сложены. Результат ID: {}", result.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("addFunctions BAD_REQUEST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("addFunctions INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/operations/subtract")
    public ResponseEntity<FunctionResponse> subtractFunctions(@RequestBody FunctionOperationRequest request) {
        logger.info("POST запрос на вычитание функций {} и {}",
                request.getFirstFunctionId(), request.getSecondFunctionId());
        try {
            Function result = operationService.subtractFunctions(
                    request.getFirstFunctionId(),
                    request.getSecondFunctionId()
            );

            FunctionResponse response = FunctionResponse.builder()
                    .id(result.getId())
                    .userId(result.getUserId())
                    .name(result.getName())
                    .description(result.getDescription())
                    .type(result.getType())
                    .pointCount(result.getPointCount())
                    .functionClass(result.getFunctionClass())
                    .build();

            logger.info("Функции успешно вычтены. Результат ID: {}", result.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("subtractFunctions BAD_REQUEST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("subtractFunctions INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/operations/multiply")
    public ResponseEntity<FunctionResponse> multiplyFunctions(@RequestBody FunctionOperationRequest request) {
        logger.info("POST запрос на умножение функций {} и {}",
                request.getFirstFunctionId(), request.getSecondFunctionId());
        try {
            Function result = operationService.multiplyFunctions(
                    request.getFirstFunctionId(),
                    request.getSecondFunctionId()
            );

            FunctionResponse response = FunctionResponse.builder()
                    .id(result.getId())
                    .userId(result.getUserId())
                    .name(result.getName())
                    .description(result.getDescription())
                    .type(result.getType())
                    .pointCount(result.getPointCount())
                    .functionClass(result.getFunctionClass())
                    .build();

            logger.info("Функции успешно умножены. Результат ID: {}", result.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("multiplyFunctions BAD_REQUEST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("multiplyFunctions INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/operations/divide")
    public ResponseEntity<FunctionResponse> divideFunctions(@RequestBody FunctionOperationRequest request) {
        logger.info("POST запрос на деление функции {} на {}",
                request.getFirstFunctionId(), request.getSecondFunctionId());
        try {
            Function result = operationService.divideFunctions(
                    request.getFirstFunctionId(),
                    request.getSecondFunctionId()
            );

            FunctionResponse response = FunctionResponse.builder()
                    .id(result.getId())
                    .userId(result.getUserId())
                    .name(result.getName())
                    .description(result.getDescription())
                    .type(result.getType())
                    .pointCount(result.getPointCount())
                    .functionClass(result.getFunctionClass())
                    .build();

            logger.info("Функции успешно разделены. Результат ID: {}", result.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("divideFunctions BAD_REQUEST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("divideFunctions INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/differentiate")
    public ResponseEntity<FunctionResponse> differentiateFunction(@RequestBody DifferentiationRequest request) {
        logger.info("POST запрос на дифференцирование функции {} по переменной {}",
                request.getFunctionId(), request.getVariable());
        try {
            Function result = operationService.differentiateFunction(
                    request.getFunctionId(),
                    request.getVariable()
            );

            FunctionResponse response = FunctionResponse.builder()
                    .id(result.getId())
                    .userId(result.getUserId())
                    .name(result.getName())
                    .description(result.getDescription())
                    .type(result.getType())
                    .pointCount(result.getPointCount())
                    .functionClass(result.getFunctionClass())
                    .build();

            logger.info("Функция успешно продифференцирована. Результат ID: {}", result.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("differentiateFunction BAD_REQUEST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("differentiateFunction INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/integrate")
    public ResponseEntity<FunctionResponse> integrateFunction(@RequestBody IntegrationRequest request) {
        logger.info("POST запрос на интегрирование функции {} от {} до {} по переменной {}",
                request.getFunctionId(), request.getLowerLimit(), request.getUpperLimit(), request.getVariable());
        try {
            Function result = operationService.integrateFunction(
                    request.getFunctionId(),
                    request.getVariable(),
                    request.getLowerLimit(),
                    request.getUpperLimit()
            );

            FunctionResponse response = FunctionResponse.builder()
                    .id(result.getId())
                    .userId(result.getUserId())
                    .name(result.getName())
                    .description(result.getDescription())
                    .type(result.getType())
                    .pointCount(result.getPointCount())
                    .functionClass(result.getFunctionClass())
                    .build();

            logger.info("Функция успешно проинтегрирована. Результат ID: {}", result.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("integrateFunction BAD_REQUEST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("integrateFunction INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/composite")
    public ResponseEntity<FunctionResponse> createCompositeFunction(@RequestBody FunctionOperationRequest request) {
        logger.info("POST запрос на создание композитной функции f(g(x)) где f={}, g={}",
                request.getFirstFunctionId(), request.getSecondFunctionId());
        try {
            Function result = operationService.createCompositeFunction(
                    request.getFirstFunctionId(),
                    request.getSecondFunctionId()
            );

            FunctionResponse response = FunctionResponse.builder()
                    .id(result.getId())
                    .userId(result.getUserId())
                    .name(result.getName())
                    .description(result.getDescription())
                    .type(result.getType())
                    .pointCount(result.getPointCount())
                    .functionClass(result.getFunctionClass())
                    .build();

            logger.info("Композитная функция успешно создана. Результат ID: {}", result.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("createCompositeFunction BAD_REQUEST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("createCompositeFunction INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/math/list")
    public ResponseEntity<FunctionListResponse> getMathFunctionList() {
        logger.info("GET запрос на получение списка математических функций");
        try {
            List<String> functions = operationService.getAvailableMathFunctions();

            FunctionListResponse response = FunctionListResponse.builder()
                    .availableFunctions(functions)
                    .totalCount(functions.size())
                    .build();

            logger.info("Список математических функций успешно получен. Количество: {}", functions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("getMathFunctionList INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create/from-math")
    public ResponseEntity<FunctionResponse> createFunctionFromMath(@RequestBody MathFunctionRequest request) {
        logger.info("POST запрос на создание функции из класса: {}", request.getFunctionClass());
        try {
            Function result = operationService.createFunctionFromMathExpression(
                    request.getUserId(),
                    request.getName(),
                    request.getDescription(),
                    request.getFunctionClass()
            );

            FunctionResponse response = FunctionResponse.builder()
                    .id(result.getId())
                    .userId(result.getUserId())
                    .name(result.getName())
                    .description(result.getDescription())
                    .type(result.getType())
                    .pointCount(result.getPointCount())
                    .functionClass(result.getFunctionClass())
                    .build();

            logger.info("Функция успешно создана из класса. ID: {}", result.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("createFunctionFromMath BAD_REQUEST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("createFunctionFromMath INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create/from-points")
    public ResponseEntity<FunctionResponse> createFunctionFromPoints(@RequestBody FunctionFromPointsRequest request) {
        logger.info("POST запрос на создание функции из {} точек",
                request.getPoints() != null ? request.getPoints().size() : 0);
        try {
            List<Point> points = request.getPoints().stream()
                    .map(p -> Point.builder()
                            .x(p.getX())
                            .y(p.getY())
                            .index(p.getIndex())
                            .build())
                    .collect(Collectors.toList());

            Function result = operationService.createFunctionFromPoints(
                    request.getUserId(),
                    request.getName(),
                    points
            );

            FunctionResponse response = FunctionResponse.builder()
                    .id(result.getId())
                    .userId(result.getUserId())
                    .name(result.getName())
                    .description(result.getDescription())
                    .type(result.getType())
                    .pointCount(result.getPointCount())
                    .functionClass(result.getFunctionClass())
                    .build();

            logger.info("Функция успешно создана из точек. ID: {}", result.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("createFunctionFromPoints BAD_REQUEST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("createFunctionFromPoints INTERNAL_SERVER_ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
