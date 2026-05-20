package com.cts.schedule_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateScheduleDTO {
    @NotNull(message = "Title ID is required")
    public Integer titleId;
    @NotNull(message = "Contract ID is required")
    public Long contractId;
    @NotBlank(message = "Platform cannot be empty")
    public String platform;
    @NotNull(message = "Start time is required")
    public LocalDateTime startDateTime;
    @NotNull(message = "End time is required")
    public LocalDateTime endDateTime;
    @NotNull(message = "Window type is required")
    public String windowType;



}
