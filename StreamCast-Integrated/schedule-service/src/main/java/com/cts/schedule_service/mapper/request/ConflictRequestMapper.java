package com.cts.schedule_service.mapper.request;

import com.cts.schedule_service.entity.Conflict;

import java.time.LocalDateTime;

public class ConflictRequestMapper {

    private ConflictRequestMapper() {}

    public static Conflict toEntity(Long scheduleId1, Long scheduleId2) {
        Conflict conflict = new Conflict();
        conflict.setScheduleId1(scheduleId1);
        conflict.setScheduleId2(scheduleId2);
        conflict.setDetectedAt(LocalDateTime.now());
        conflict.setResolved(false);
        return conflict;
    }
}