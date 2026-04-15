package com.Cts.service;
import com.Cts.dto.request.LoginRequest;
import com.Cts.dto.request.UserRequestDTO;
import com.Cts.dto.response.LoginResponseDTO;
import com.Cts.dto.response.UserResponseDTO;

public interface AuthService {
    UserResponseDTO register(UserRequestDTO dto);
    LoginResponseDTO login(LoginRequest request);
}