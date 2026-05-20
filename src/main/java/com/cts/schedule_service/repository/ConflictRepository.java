package com.cts.schedule_service.repository;

import com.cts.schedule_service.entity.Conflict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConflictRepository extends JpaRepository<Conflict, Long> {

    // Fetch conflicts where the schedule participates as either side
    List<Conflict> findByScheduleId1OrScheduleId2(Long scheduleId1, Long scheduleId2);
    boolean existsByScheduleId1AndScheduleId2(Long scheduleId1, Long scheduleId2);
}