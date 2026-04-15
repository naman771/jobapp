package com.jobportal.service;

import com.jobportal.dto.*;
import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
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
        if (req.getSecurityQuestion() != null && req.getSecurityAnswer() != null) {
            u.setSecurityQuestion(req.getSecurityQuestion());
            u.setSecurityAnswer(passwordEncoder.encode(req.getSecurityAnswer().trim().toLowerCase()));
        }
        userRepository.save(u);
    }

    public String login(AuthRequest req) {
        Optional<User> ou = userRepository.findByEmail(req.getEmail());
        if (ou.isEmpty()) throw new RuntimeException("Invalid credentials");
        User u = ou.get();
        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) throw new RuntimeException("Invalid credentials");
        return jwtUtil.generateToken(u.getEmail());
    }

    public LoginResponse loginWithUserInfo(AuthRequest req) {
        Optional<User> ou = userRepository.findByEmail(req.getEmail());
        if (ou.isEmpty()) throw new RuntimeException("Invalid credentials");
        User u = ou.get();
        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) throw new RuntimeException("Invalid credentials");
        String token = jwtUtil.generateToken(u.getEmail());

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
            u.getId(),
            u.getName(),
            u.getEmail(),
            u.getRole()
        );

        return new LoginResponse(token, userInfo);
    }

    public Map<String, Object> getUserByEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return Map.of(
            "id", u.getId(),
            "name", u.getName(),
            "email", u.getEmail(),
            "role", u.getRole()
        );
    }

    // Forgot password: get the security question for an email
    public String getSecurityQuestion(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));
        if (u.getSecurityQuestion() == null || u.getSecurityQuestion().isBlank()) {
            throw new RuntimeException("No security question set for this account");
        }
        return u.getSecurityQuestion();
    }

    // Forgot password: verify answer and reset
    public void resetPassword(ForgotPasswordRequest req) {
        User u = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        if (u.getSecurityAnswer() == null) {
            throw new RuntimeException("No security question set for this account");
        }

        if (!passwordEncoder.matches(req.getSecurityAnswer().trim().toLowerCase(), u.getSecurityAnswer())) {
            throw new RuntimeException("Incorrect security answer");
        }

        if (req.getNewPassword() == null || req.getNewPassword().length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        u.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(u);
    }
}
