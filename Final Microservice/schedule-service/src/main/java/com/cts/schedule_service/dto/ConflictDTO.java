package com.cts.schedule_service.dto;

import com.cts.schedule_service.entity.Schedule;

import java.time.LocalDateTime;

public class ConflictDTO {

    public Long conflictId;
    public Long scheduleId1;
    public Long scheduleId2;
    public LocalDateTime detectedAt;
    public boolean resolved;
}

