package com.cts.usage_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateUsageDTO {

    @NotNull(message = "Title ID is required")
    public Long titleId;

    @NotBlank(message = "Platform is required")
    public String platform;

    @NotNull(message = "Date is required")
    public LocalDate date;

    @NotNull(message = "Views is required")
    @Min(value = 0, message = "Views cannot be negative")
    public Long views;

    @NotNull(message = "Revenue is required")
    @DecimalMin(value = "0.0", message = "Revenue cannot be negative")
    public Double revenue;
}