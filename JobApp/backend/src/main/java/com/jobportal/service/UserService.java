package com.jobportal.service;

import com.jobportal.dto.*;
import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository ur, PasswordEncoder pe, JwtUtil ju){
        this.userRepository = ur;
        this.passwordEncoder = pe;
        this.jwtUtil = ju;
    }

    public void register(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent())
            throw new RuntimeException("Email already exists");
        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(req.getRole() == null ? "ROLE_CANDIDATE" : req.getRole());
        userRepository.save(u);
    }

    public String login(AuthRequest req) {
        Optional<User> ou = userRepository.findByEmail(req.getEmail());
        if (ou.isEmpty()) throw new RuntimeException("Invalid credentials");
        User u = ou.get();
        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) throw new RuntimeException("Invalid credentials");
        return jwtUtil.generateToken(u.getEmail());
    }

    public com.jobportal.dto.LoginResponse loginWithUserInfo(AuthRequest req) {
        Optional<User> ou = userRepository.findByEmail(req.getEmail());
        if (ou.isEmpty()) throw new RuntimeException("Invalid credentials");
        User u = ou.get();
        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) throw new RuntimeException("Invalid credentials");
        String token = jwtUtil.generateToken(u.getEmail());
        
        com.jobportal.dto.LoginResponse.UserInfo userInfo = new com.jobportal.dto.LoginResponse.UserInfo(
            u.getId(),
            u.getName(),
            u.getEmail(),
            u.getRole()
        );
        
        return new com.jobportal.dto.LoginResponse(token, userInfo);
    }

    public java.util.Map<String, Object> getUserByEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return java.util.Map.of(
            "id", u.getId(),
            "name", u.getName(),
            "email", u.getEmail(),
            "role", u.getRole()
        );
    }
}
