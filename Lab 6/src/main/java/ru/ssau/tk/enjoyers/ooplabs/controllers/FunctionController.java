package ru.ssau.tk.enjoyers.ooplabs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.services.FunctionService;

@RestController
public class FunctionController {

    @Autowired
    private FunctionService functionService;

    @GetMapping("/functions/{id}")
    public ResponseEntity<Function> getFunctionById(@PathVariable Long id) {
        try {
            return functionService.getFunction(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/functions/{id}")
    public ResponseEntity<Function> createFunction(@RequestBody Function function) {
        try {
            Function newFunction = functionService.createFunction(function);
            return ResponseEntity.status(HttpStatus.CREATED).body(newFunction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/functions/{id}")
    public ResponseEntity<Function> updateFunction(@PathVariable Long id, @RequestBody Function function) {
        try {
            function.setId(id);
            Function newFunction = functionService.updateFunction(id, function);
            return ResponseEntity.ok(newFunction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/functions/{id}")
    public ResponseEntity<Function> deleteFunction(@PathVariable Long id) {
        try {
            functionService.deleteFunction(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
