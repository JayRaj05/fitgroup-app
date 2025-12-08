package com.fitgroup.backend.controller;

import com.fitgroup.backend.dto.LoginRequest;
import com.fitgroup.backend.dto.RegisterRequest;
import com.fitgroup.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {

        String result = userService.registerUser(request);

        if (result.equals("EMAIL_ALREADY_EXISTS")) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {

        String token = userService.loginUserAndGenerateToken(
                request.getEmail(),
                request.getPassword()
        );

        if (token.equals("USER_NOT_FOUND")) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (token.equals("INVALID_PASSWORD")) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        return ResponseEntity.ok(token);
    }



}
