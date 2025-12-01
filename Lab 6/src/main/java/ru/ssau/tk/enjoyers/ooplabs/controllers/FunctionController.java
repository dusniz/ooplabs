package ru.ssau.tk.enjoyers.ooplabs.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.services.FunctionService;

@RestController
public class FunctionController {

    private static final Logger logger = LogManager.getLogger(FunctionController.class);

    @Autowired
    private FunctionService functionService;

    @GetMapping("/functions/{id}")
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

    @PostMapping("/functions/")
    public ResponseEntity<Function> createFunction(@RequestBody Function function) {
        logger.info("POST запрос на создание функции с данными: {}", function);
        try {
            Function newFunction = functionService.createFunction(function);
            logger.info("Функция успешно создана с ID: {}", newFunction.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(newFunction);
        } catch (Exception e) {
            logger.error("function POST INTERNAL_SERVER_ERROR {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/functions/{id}")
    public ResponseEntity<Function> updateFunction(@PathVariable("id") Long id, @RequestBody Function function) {
        logger.info("PUT запрос на обновление функции с ID: {}. Данные: {}", id, function);
        try {
            function.setId(id);
            Function newFunction = functionService.updateFunction(id, function);
            logger.info("Функция с ID: {} успешно обновлена", id);
            return ResponseEntity.ok(newFunction);
        } catch (IllegalArgumentException e) {
            logger.error("function PUT BAD_REQUEST ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("function PUT INTERNAL_SERVER_ERROR {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/functions/{id}")
    public ResponseEntity<Function> deleteFunction(@PathVariable("id") Long id) {
        try {
            functionService.deleteFunction(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("function DELETE BAD_REQUEST ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("function DELETE INTERNAL_SERVER_ERROR {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
