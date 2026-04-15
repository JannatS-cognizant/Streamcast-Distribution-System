package com.Cts.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

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