package ru.ssau.tk.enjoyers.ooplabs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.services.PointService;

@RestController
public class PointController {

    @Autowired
    private PointService pointService;

    @GetMapping("/points/{id}")
    public ResponseEntity<Point> getPoint(@PathVariable Long id) {
        try {
            return pointService.getPoint(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/points/{id}")
    public ResponseEntity<Point> createPoint(@RequestBody Point point) {
        try {
            Point newPoint = pointService.createPoint(point.getFunctionId(), point.getX(), point.getY(), point.getIndex());
            return ResponseEntity.status(HttpStatus.CREATED).body(newPoint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/points/{id}")
    public ResponseEntity<Point> updatePoint(@PathVariable Long id, @RequestBody Point point) {
        try {
            Point newPoint = pointService.updatePoint(id, point.getX(), point.getY());
            return ResponseEntity.ok(newPoint);
        } catch (IllegalArgumentException e) {
            // Убедиться в работоспособности
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/points/{id}")
    public ResponseEntity<Point> deletePoint(@PathVariable Long id) {
        try {
            pointService.deletePoint(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
