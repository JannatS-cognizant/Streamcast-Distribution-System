package com.cts.schedule_service.mapper.response;

import com.cts.schedule_service.dto.CalendarScheduleDTO;
import com.cts.schedule_service.entity.Schedule;

public class ScheduleResponseMapper {

    private ScheduleResponseMapper() {}

    // Entity → Response DTO
    public static CalendarScheduleDTO toDTO(Schedule s) {
        CalendarScheduleDTO dto = new CalendarScheduleDTO();
        dto.scheduleId = s.getScheduleId();
        dto.platform = s.getPlatform();
        dto.startDateTime = s.getStartDateTime();
        dto.endDateTime = s.getEndDateTime();
        dto.status = s.getStatus();
        return dto;
    }
}

