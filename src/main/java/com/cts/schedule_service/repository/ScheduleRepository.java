package com.cts.schedule_service.repository;

import com.cts.schedule_service.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // ✅ Calendar view — find schedules in date range
    List<Schedule> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);

    // ✅ Existing window query
    @Query("""
       SELECT s FROM Schedule s
       WHERE s.startDateTime >= :start
         AND s.endDateTime <= :end
       """)
    List<Schedule> findSchedulesWithinWindow(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // ✅ Business Logic 1: Find overlapping schedules for same title + platform
    // Used in createSchedule() to prevent duplicate schedules
    @Query("""
        SELECT s FROM Schedule s
        WHERE s.titleId = :titleId
        AND LOWER(s.platform) = LOWER(:platform)
        AND s.startDateTime < :endDateTime
        AND s.endDateTime > :startDateTime
    """)
    List<Schedule> findOverlapping(
            @Param("titleId") Long titleId,
            @Param("platform") String platform,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    // ✅ Business Logic 6: Find ALL overlapping schedules for same title (any platform)
    // Used in exclusivity conflict detection
    @Query("""
        SELECT s FROM Schedule s
        WHERE s.titleId = :titleId
        AND s.scheduleId != :excludeId
        AND s.startDateTime < :endDateTime
        AND s.endDateTime > :startDateTime
    """)
    List<Schedule> findByTitleIdAndOverlapping(
            @Param("titleId") Long titleId,
            @Param("excludeId") Long excludeId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}