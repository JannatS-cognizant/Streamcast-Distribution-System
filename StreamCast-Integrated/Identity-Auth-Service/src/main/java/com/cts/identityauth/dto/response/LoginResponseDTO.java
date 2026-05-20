package com.cts.identityauth.dto.response;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private UserResponseDTO user;
}