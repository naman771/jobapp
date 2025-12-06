package com.jobportal.controller;

import com.jobportal.dto.*;
import com.jobportal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final UserService userService;
    public AuthController(UserService us){ this.userService = us; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req){
        userService.register(req);
        return ResponseEntity.ok().body(java.util.Map.of("message", "registered", "success", true));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req){
        return ResponseEntity.ok(userService.loginWithUserInfo(req));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body(java.util.Map.of("error", "Not authenticated"));
        }
        return ResponseEntity.ok(userService.getUserByEmail(auth.getName()));
    }
}
