package com.cts.complianceservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplianceRequestDTO {

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    @NotBlank(message = "Compliance result is required")
    private String result;

    @NotBlank(message = "Notes are required")
    private String notes;
}