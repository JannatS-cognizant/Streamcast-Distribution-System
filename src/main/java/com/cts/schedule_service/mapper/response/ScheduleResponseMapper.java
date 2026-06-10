package com.cts.schedule_service.mapper.response;

import com.cts.schedule_service.dto.CalendarScheduleDTO;
import com.cts.schedule_service.entity.Schedule;

import java.time.LocalDateTime;

public class ScheduleResponseMapper {

    private ScheduleResponseMapper() {}

    public static CalendarScheduleDTO toDTO(Schedule s) {
        CalendarScheduleDTO dto = new CalendarScheduleDTO();
        dto.scheduleId    = s.getScheduleId();
        dto.titleId       = s.getTitleId();
        dto.contractId    = s.getContractId();
        dto.platform      = s.getPlatform();
        dto.windowType    = s.getWindowtype();
        dto.startDateTime = s.getStartDateTime();
        dto.endDateTime   = s.getEndDateTime();

        // ✅ Business Logic 4: Auto-expiry — if end time passed and still ACTIVE
        // return EXPIRED dynamically without changing DB
        if (s.getEndDateTime().isBefore(LocalDateTime.now())
                && "ACTIVE".equalsIgnoreCase(s.getStatus())) {
            dto.status = "EXPIRED";
        } else {
            dto.status = s.getStatus();
        }

        return dto;
    }
}