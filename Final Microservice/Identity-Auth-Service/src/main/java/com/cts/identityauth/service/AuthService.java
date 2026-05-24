package com.cts.identityauth.service;

import com.cts.identityauth.dto.request.LoginRequest;
import com.cts.identityauth.dto.request.UserRequestDTO;
import com.cts.identityauth.dto.response.LoginResponseDTO;
import com.cts.identityauth.dto.response.UserResponseDTO;

public interface AuthService {
    UserResponseDTO register(UserRequestDTO dto);
    LoginResponseDTO login(LoginRequest request);
    LoginResponseDTO adminLogin(LoginRequest request);
    void verifyEmail(String token);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
    void sendUsername(String email);
}