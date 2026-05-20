package com.cts.schedule_service.mapper.response;

import com.cts.schedule_service.dto.ConflictDTO;
import com.cts.schedule_service.entity.Conflict;

public class ConflictResponseMapper {

    private ConflictResponseMapper() {}

    public static ConflictDTO toDTO(Conflict conflict) {
        ConflictDTO dto = new ConflictDTO();
        dto.conflictId = conflict.getConflictId();
        dto.scheduleId1 = conflict.getScheduleId1();
        dto.scheduleId2 = conflict.getScheduleId2();
        dto.detectedAt = conflict.getDetectedAt();
        dto.resolved = conflict.isResolved();
        return dto;
    }
}