package com.cts.schedule_service.mapper.request;

import com.cts.schedule_service.dto.CreateScheduleDTO;
import com.cts.schedule_service.entity.Schedule;

public class ScheduleRequestMapper {

    private ScheduleRequestMapper() {}

    // Create request → Entity
    public static Schedule toEntity(CreateScheduleDTO dto) {
        Schedule s = new Schedule();
        s.setTitleId(dto.titleId);
        s.setContractId(dto.contractId);
        s.setPlatform(dto.platform);
        s.setStartDateTime(dto.startDateTime);
        s.setEndDateTime(dto.endDateTime);
        s.setWindowtype(dto.windowType);
        s.setStatus("ACTIVE");
        return s;
    }

    // Update request → Existing Entity
    public static void updateEntity(Schedule existing, CreateScheduleDTO dto) {
        existing.setPlatform(dto.platform);
        existing.setStartDateTime(dto.startDateTime);
        existing.setEndDateTime(dto.endDateTime);
        existing.setWindowtype(dto.windowType);
    }
}

