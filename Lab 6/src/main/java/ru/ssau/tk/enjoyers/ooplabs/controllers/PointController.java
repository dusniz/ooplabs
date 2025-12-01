package ru.ssau.tk.enjoyers.ooplabs.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.services.PointService;

@RestController
@RequestMapping("/api/v1")
public class PointController {

    private static final Logger logger = LogManager.getLogger(PointController.class);

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
