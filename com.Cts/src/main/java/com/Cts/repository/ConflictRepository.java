package com.Cts.repository;

import com.Cts.entity.Conflict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConflictRepository extends JpaRepository<Conflict,Long> {

    List<Conflict> findByScheduleId(Long scheduleId);
}
