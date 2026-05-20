package com.cts.streamcast.controller;

import com.cts.streamcast.dto.LoginRequest;
import com.cts.streamcast.dto.UserRequestDTO;
import com.cts.streamcast.entity.Role;
import com.cts.streamcast.entity.User;
import com.cts.streamcast.repository.RoleRepository;
import com.cts.streamcast.repository.UserRepository;
import com.cts.streamcast.service.UserService;

import jakarta.validation.Valid;

import com.cts.streamcast.auth.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;
    
    private final UserService userService;

    public AuthController(UserRepository userRepo, RoleRepository roleRepo, JwtUtil jwtUtil, BCryptPasswordEncoder encoder, UserService userService) {
        this.userRepo = userRepo;
        this.roleRepo=roleRepo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String role = user.getRole().getName().toUpperCase().replace(" ", "_");
        return jwtUtil.generateToken(user.getEmail(), role);
       
    }
    
    @PostMapping("/register")
    public User register(@Valid @RequestBody UserRequestDTO dto) {

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));

        Role role = roleRepo.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        String token = java.util.UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setTokenExpiry(java.time.LocalDateTime.now().plusHours(24));
        user.setEmailVerified(false);

        User saved = userRepo.save(user);

        try {
            userService.sendVerificationEmail(saved.getEmail(), token);
        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
            System.out.println("Verification token: " + token);
            System.out.println("Verify at: http://localhost:8081/auth/verify?token=" + token);
        }

        return saved;
    }
    
    
    
    
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return "Email verified successfully";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return "Password reset link sent";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return "Password updated successfully";
    }

    @PostMapping("/forgot-username")
    public String forgotUsername(@RequestParam String email) {
        userService.sendUsername(email);
        return "Username sent to email";
    }
    
}

