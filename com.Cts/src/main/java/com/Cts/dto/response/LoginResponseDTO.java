package com.Cts.dto.response;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private UserResponseDTO user;
}