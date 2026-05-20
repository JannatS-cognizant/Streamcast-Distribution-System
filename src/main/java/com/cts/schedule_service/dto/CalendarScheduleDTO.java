package com.cts.schedule_service.dto;

import java.time.LocalDateTime;

public class CalendarScheduleDTO {

    public Long scheduleId;
    public Long titleId;       // added — was missing
    public Long contractId;       // added — was missing
    public String platform;
    public String windowType;     // added — was missing
    public LocalDateTime startDateTime;
    public LocalDateTime endDateTime;
    public String status;
}