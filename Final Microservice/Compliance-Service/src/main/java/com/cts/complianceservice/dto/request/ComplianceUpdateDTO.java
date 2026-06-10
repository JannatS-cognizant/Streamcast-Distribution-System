package com.cts.complianceservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComplianceUpdateDTO {

    @NotBlank(message = "Result is required")
    private String result;

    @NotBlank(message = "Notes are required")
    private String notes;
}