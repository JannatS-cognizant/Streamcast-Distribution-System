package com.cts.schedule_service.repository;


import com.cts.schedule_service.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {


    List<Schedule> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);
    @Query("""
       SELECT s
       FROM Schedule s
       WHERE s.startDateTime >= :start
         AND s.endDateTime <= :end
       """)
    List<Schedule> findSchedulesWithinWindow(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
