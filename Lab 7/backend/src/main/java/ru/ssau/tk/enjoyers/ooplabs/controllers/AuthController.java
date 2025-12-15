package ru.ssau.tk.enjoyers.ooplabs.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ssau.tk.enjoyers.ooplabs.dto.JwtAuthResponse;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserSignInRequest;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserSignUpRequest;
import ru.ssau.tk.enjoyers.ooplabs.services.AuthService;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> register(@RequestBody UserSignUpRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtAuthResponse> authenticate(@RequestBody UserSignInRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
