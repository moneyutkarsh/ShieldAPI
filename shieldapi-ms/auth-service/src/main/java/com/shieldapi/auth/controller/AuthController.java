package com.shieldapi.auth.controller;

import com.shieldapi.auth.service.AuthService;
import com.shieldapi.common.model.UserRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request.getUsername(), request.getPassword(), request.getEmail(), request.getRoles());
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private Set<UserRole> roles;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
