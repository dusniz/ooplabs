package ru.ssau.tk.enjoyers.ooplabs.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.enjoyers.ooplabs.dto.PointResponse;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.services.FunctionService;
import ru.ssau.tk.enjoyers.ooplabs.services.PointService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PointController {

    private static final Logger logger = LogManager.getLogger(PointController.class);

    @Autowired
    private FunctionService functionService;

    @Autowired
    private PointService pointService;

    @GetMapping("/points/{id}")
    public ResponseEntity<Point> getPoint(@PathVariable("id") Long id) {
        logger.info("GET запрос на получение точки с ID: {}", id);
        try {
            return pointService.getPoint(id).map(point -> {
                logger.info("Точка с ID: {} успешно найдена. Данные: {}", id, point);
                return ResponseEntity.ok(point);
            }).orElseGet(() -> {
                logger.info("Точка с ID: {} не найдена.", id);
                return ResponseEntity.notFound().build();});
        } catch (IllegalArgumentException e) {
            logger.error("point GET BAD_REQUEST ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("point GET INTERNAL_SERVER_ERROR ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/points/functionId/{id}")
    public ResponseEntity<List<Point>> getPointsByFunctionId(@PathVariable("id") Long functionId) {
        logger.info("GET запрос на получение всех точек фукнции с ID: {}", functionId);
        try {
            Optional<Function> function = functionService.getFunction(functionId);
            List<Point> points = List.of();
            if (function.isPresent() && function.get().getType().equals("TABULATED")) {
                points = pointService.getPointsByFunctionId(functionId);
                logger.info("Найдено {} точек функции с ID: {}", points.size(), functionId);
            } else if (function.isPresent()) {
                ArrayList<Point> temp = new ArrayList<Point>();
                for (int x = -10; x <= 10; x++) {
                    temp.add(new Point(null, functionId, (double) x, functionService.functionEvaluate(function.get(), x), x + 10));
                }
                points = temp;
            }
            return ResponseEntity.ok(points);
        } catch (IllegalArgumentException e) {
            logger.error("point GET BAD_REQUEST by function ID: {} {}", functionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("point GET INTERNAL_SERVER_ERROR by function ID: {} {}", functionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/points")
    public ResponseEntity<Point> createPoint(@RequestBody Point point) {
        logger.info("POST запрос на создание точки с данными: {}", point);
        try {
            Point newPoint = pointService.createPoint(point.getFunctionId(), point.getX(), point.getY(), point.getIndex());
            logger.info("Точка успешно создана с ID: {}", newPoint.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(newPoint);
        } catch (Exception e) {
            logger.error("point POST INTERNAL_SERVER_ERROR {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/points/{id}")
    public ResponseEntity<Point> updatePoint(@PathVariable("id") Long id, @RequestBody Point point) {
        logger.info("PUT запрос на обновление точки с ID: {}", id);
        try {
            Point updatedPoint = pointService.updatePoint(id, point.getX(), point.getY());
            logger.info("Точка с ID: {} успешно обновлена", id);
            return ResponseEntity.ok(updatedPoint);
        } catch (IllegalArgumentException e) {
            // Убедиться в работоспособности
            logger.error("point PUT BAD_REQUEST {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("point PUT INTERNAL_SERVER_ERROR {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/points/{id}")
    public ResponseEntity<Point> deletePoint(@PathVariable("id") Long id) {
        logger.info("DELETE запрос на удаление точки с ID: {}", id);
        try {
            pointService.deletePoint(id);
            logger.info("Точка с ID: {} успешно удалена", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("point DELETE BAD_REQUEST {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("point DELETE INTERNAL_SERVER_ERROR {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
