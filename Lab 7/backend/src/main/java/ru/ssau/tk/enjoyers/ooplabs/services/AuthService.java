package ru.ssau.tk.enjoyers.ooplabs.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.tk.enjoyers.ooplabs.Role;
import ru.ssau.tk.enjoyers.ooplabs.dto.JwtAuthResponse;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserResponse;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserSignInRequest;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserSignUpRequest;
import ru.ssau.tk.enjoyers.ooplabs.entities.User;
import ru.ssau.tk.enjoyers.ooplabs.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthResponse register(UserSignUpRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtAuthResponse.builder()
                .token(jwt)
                .user(new UserResponse(user.getId(), user.getUsername(), user.getRole()))
                .build();
    }

    public JwtAuthResponse authenticate(UserSignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Не правильно!"));
        var jwt = jwtService.generateToken(user);
        return JwtAuthResponse.builder()
                .token(jwt)
                .user(new UserResponse(user.getId(), user.getUsername(), user.getRole()))
                .build();
    }
}
