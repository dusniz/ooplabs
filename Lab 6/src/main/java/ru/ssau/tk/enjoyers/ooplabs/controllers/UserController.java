package ru.ssau.tk.enjoyers.ooplabs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.enjoyers.ooplabs.entities.User;
import ru.ssau.tk.enjoyers.ooplabs.services.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

//    @GetMapping("/users/{id}")
//    public ResponseEntity<User> getUser(@PathVariable Long id) {
//        try {
//            return userService.getUser(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @PostMapping("/users/{id}")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User newUser = userService.createUser(user.getUsername(), user.getPasswordHash(), user.getRole());
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @PutMapping("/users/{id}")
//    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
//        try {
//            User newPoint = userService.updateUser(id, user.getUsername(), user.getPasswordHash());
//            return ResponseEntity.ok(newPoint);
//        } catch (IllegalArgumentException e) {
//            // Убедиться в работоспособности
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
