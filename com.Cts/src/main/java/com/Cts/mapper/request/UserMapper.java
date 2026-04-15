package com.Cts.mapper.request;

import com.Cts.dto.response.UserResponseDTO;
import com.Cts.entity.User;

public class UserMapper {

    public static UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().getName());
        dto.setEmailVerified(user.isEmailVerified());
        return dto;
    }
}