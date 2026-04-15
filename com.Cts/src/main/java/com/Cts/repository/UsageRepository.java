package com.Cts.repository;

import com.Cts.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface UsageRepository extends JpaRepository<UsageRecord, Long> {
    @Query("""
            SELECT SUM(u.views),SUM(u.revenue),COUNT(u)
            FROM UsageRecord u
            WHERE u.date BETWEEN :start AND :end
            """)
    Object getSummary(LocalDate start, LocalDate end);

    @Query("""
            SELECT u.platform, SUM(u.views),SUM(u.revenue)
            FROM UsageRecord u
            WHERE u.date BETWEEN :start AND :end
            GROUP BY u.platform
            """)
    List<Object[]>  getPlatformBreakdown(LocalDate start, LocalDate end);

    @Query("""
            SELECT u.titleId, SUM(u.views),SUM(u.revenue)
            FROM UsageRecord u
            WHERE u.date BETWEEN :start AND :end
            GROUP BY u.titleId
            """)
    List<Object[]>  getTitleBreakdown(LocalDate start, LocalDate end);

    List<UsageRecord> findByDateBetween(LocalDate start, LocalDate end);

}
