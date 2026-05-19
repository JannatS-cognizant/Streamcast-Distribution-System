package com.cts.usage_service.repository;

import com.cts.usage_service.dto.UsageBreakdownDTO;
import com.cts.usage_service.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UsageRepository extends JpaRepository<UsageRecord, Long> {

    // ✅ DETAILS
    List<UsageRecord> findByDateBetween(LocalDate start, LocalDate end);

    // ✅ SUMMARY
    @Query("""
           SELECT COALESCE(SUM(u.views), 0)
           FROM UsageRecord u
           WHERE u.date BETWEEN :start AND :end
           """)
    Long sumViews(@Param("start") LocalDate start,
                  @Param("end") LocalDate end);

    @Query("""
           SELECT COALESCE(SUM(u.revenue), 0)
           FROM UsageRecord u
           WHERE u.date BETWEEN :start AND :end
           """)
    Double sumRevenue(@Param("start") LocalDate start,
                      @Param("end") LocalDate end);

    @Query("""
           SELECT COUNT(u)
           FROM UsageRecord u
           WHERE u.date BETWEEN :start AND :end
           """)
    Long countRecords(@Param("start") LocalDate start,
                      @Param("end") LocalDate end);

    // ✅ BREAKDOWN BY PLATFORM
    @Query("""
           SELECT new com.cts.usage_service.dto.UsageBreakdownDTO(
               u.platform,
               SUM(u.views),
               SUM(u.revenue)
           )
           FROM UsageRecord u
           WHERE u.date BETWEEN :start AND :end
           GROUP BY u.platform
           """)
    List<UsageBreakdownDTO> groupByPlatform(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // ✅ BREAKDOWN BY TITLE (FIXED ✅)
    @Query("""
           SELECT new com.cts.usage_service.dto.UsageBreakdownDTO(
               u.titleId,
               SUM(u.views),
               SUM(u.revenue)
           )
           FROM UsageRecord u
           WHERE u.date BETWEEN :start AND :end
           GROUP BY u.titleId
           """)
    List<UsageBreakdownDTO> groupByTitle(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // ✅ BREAKDOWN BY DATE
    @Query("""
           SELECT new com.cts.usage_service.dto.UsageBreakdownDTO(
               u.date,
               SUM(u.views),
               SUM(u.revenue)
           )
           FROM UsageRecord u
           WHERE u.date BETWEEN :start AND :end
           GROUP BY u.date
           """)
    List<UsageBreakdownDTO> groupByDate(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}