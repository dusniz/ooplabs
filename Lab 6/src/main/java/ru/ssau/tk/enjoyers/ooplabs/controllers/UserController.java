package ru.ssau.tk.enjoyers.ooplabs.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserRequest;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserResponse;
import ru.ssau.tk.enjoyers.ooplabs.entities.User;
import ru.ssau.tk.enjoyers.ooplabs.services.UserService;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        logger.info("GET запрос на получение пользователя с ID: {}", id);
        try {
            return userService.getUser(id).map(user -> {
                logger.info("Пользователь с ID: {} успешно найден. Имя пользователя: {}, Роль: {}",
                        id, user.getUsername(), user.getRole());
                        UserResponse userResponse = UserResponse.builder()
                        .username(user.getUsername())
                        .role(user.getRole())
                        .build();
                return ResponseEntity.ok(userResponse);
            }).orElseGet(() -> {
                logger.warn("Пользователь с ID: {} не найден", id);
                return ResponseEntity.notFound().build();
            });
        } catch (IllegalArgumentException e) {
            logger.error("user GET BAD_REQUEST ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("user GET INTERNAL_SERVER_ERROR ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/users/")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest user) {
        logger.info("POST запрос на создание пользователя");
        try {
            User newUser = userService.createUser(user.getUsername(), user.getPasswordHash(), user.getRole());
            logger.info("Пользователь успешно создан с ID: {}. Имя пользователя: {}, Роль: {}",
                    newUser.getId(), newUser.getUsername(), newUser.getRole());
            UserResponse userResponse = UserResponse.builder()
                    .username(newUser.getUsername())
                    .role(newUser.getRole())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
        } catch (Exception e) {
            logger.error("user POST INTERNAL_SERVER_ERROR {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") Long id, @RequestBody UserRequest user) {
        logger.info("PUT запрос на обновление пользователя с ID: {}", id);
        try {
            User updatedUser = userService.updateUser(id, user.getUsername(), user.getPasswordHash(), user.getRole());
            logger.info("Пользователь с ID: {} успешно обновлен. Новое имя пользователя: {}",
                    id, updatedUser.getUsername());
            UserResponse userResponse = UserResponse.builder()
                    .username(updatedUser.getUsername())
                    .role(updatedUser.getRole())
                    .build();
            return ResponseEntity.ok(userResponse);
        } catch (IllegalArgumentException e) {
            logger.error("user PUT BAD_REQUEST ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("user PUT INTERNAL_SERVER_ERROR ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable("id") Long id) {
        logger.info("DELETE запрос на удаление пользователя с ID: {}", id);
        try {
            userService.deleteUser(id);
            logger.info("Пользователь с ID: {} успешно удален", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("user DELETE BAD_REQUEST ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("user DELETE INTERNAL_SERVER_ERROR ID: {} {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
