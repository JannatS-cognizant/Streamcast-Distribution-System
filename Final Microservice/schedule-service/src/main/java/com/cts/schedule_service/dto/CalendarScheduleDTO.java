package com.cts.schedule_service.dto;

import java.time.LocalDateTime;

public class CalendarScheduleDTO {

    public Long scheduleId;
    public String platform;
    public LocalDateTime startDateTime;
    public LocalDateTime endDateTime;
    public String status;
}
