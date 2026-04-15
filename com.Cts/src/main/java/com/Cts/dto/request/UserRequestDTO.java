package com.Cts.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequestDTO {
	 
    @NotBlank(message = "Name is required")
    private String name;
 
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
 
    @NotBlank(message = "Password is required")
    private String password;
 
    @NotNull(message = "Role ID is required")
    private Long roleId;
}