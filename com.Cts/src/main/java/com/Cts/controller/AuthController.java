package com.Cts.controller;
import com.Cts.dto.request.LoginRequest;
import com.Cts.dto.request.UserRequestDTO;
import com.Cts.dto.response.LoginResponseDTO;
import com.Cts.dto.response.UserResponseDTO;
import com.Cts.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@Valid @RequestBody UserRequestDTO dto) {
        return service.register(dto);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }
}