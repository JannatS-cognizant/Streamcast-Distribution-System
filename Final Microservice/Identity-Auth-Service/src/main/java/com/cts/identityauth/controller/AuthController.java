package com.cts.identityauth.controller;
import com.cts.identityauth.dto.request.LoginRequest;
import com.cts.identityauth.dto.request.UserRequestDTO;
import com.cts.identityauth.dto.response.LoginResponseDTO;
import com.cts.identityauth.dto.response.UserResponseDTO;
import com.cts.identityauth.entity.Role;
import com.cts.identityauth.service.AuthService;
import com.cts.identityauth.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RoleService roleService;

    public AuthController(AuthService authService, RoleService roleService) {
        this.authService = authService;
        this.roleService = roleService;
    }

    /** GET /auth/roles — public list of role names/ids for the registration form. */
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRolesForRegistration() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    /** POST /auth/register */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    /** POST /auth/login → returns JWT */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /** POST /auth/admin/login → returns JWT, ADMIN role required */
    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponseDTO> adminLogin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    /** GET /auth/verify?token= */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }

    /** POST /auth/forgot-password?email= */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok("Password reset link sent to your email");
    }

    /** POST /auth/reset-password?token=&newPassword= */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                 @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }

    /** POST /auth/forgot-username?email= */
    @PostMapping("/forgot-username")
    public ResponseEntity<String> forgotUsername(@RequestParam String email) {
        authService.sendUsername(email);
        return ResponseEntity.ok("Username sent to your email");
    }
}