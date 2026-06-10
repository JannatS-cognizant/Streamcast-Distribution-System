package com.cts.schedule_service.service;

import com.cts.schedule_service.dto.ConflictDTO;

import java.util.List;

public interface ConflictService {

    void detectConflicts(Long scheduleId);

    List<ConflictDTO> getConflictBySchedule(Long scheduleId);

    ConflictDTO getConflictById(Long id);

    ConflictDTO resolveConflict(Long id);
}