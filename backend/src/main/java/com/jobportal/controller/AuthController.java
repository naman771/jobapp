package com.jobportal.controller;

import com.jobportal.dto.*;
import com.jobportal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final UserService userService;
    public AuthController(UserService us){ this.userService = us; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req){
        userService.register(req);
        return ResponseEntity.ok().body(Map.of("message", "registered", "success", true));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req){
        return ResponseEntity.ok(userService.loginWithUserInfo(req));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        return ResponseEntity.ok(userService.getUserByEmail(auth.getName()));
    }

    // Forgot password step 1: get security question
    @PostMapping("/forgot-password/question")
    public ResponseEntity<?> getSecurityQuestion(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            String question = userService.getSecurityQuestion(email);
            return ResponseEntity.ok(Map.of("securityQuestion", question));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Forgot password step 2: verify answer and reset password
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ForgotPasswordRequest req) {
        try {
            userService.resetPassword(req);
            return ResponseEntity.ok(Map.of("message", "Password reset successful", "success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
