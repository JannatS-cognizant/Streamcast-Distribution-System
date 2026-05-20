package com.cts.usage_service.service;

import com.cts.usage_service.dto.CreateUsageDTO;
import com.cts.usage_service.dto.UsageBreakdownDTO;
import com.cts.usage_service.dto.UsageDetailsDTO;
import com.cts.usage_service.dto.UsageSummaryDTO;

import java.time.LocalDate;
import java.util.List;

public interface UsageService {

    // ✅ existing read endpoints
    UsageSummaryDTO getSummary(LocalDate start, LocalDate end);
    List<UsageDetailsDTO> getDetails(LocalDate start, LocalDate end);
    List<UsageBreakdownDTO> getBreakdown(LocalDate start, LocalDate end, String groupBy);

    // ✅ write endpoints
    UsageDetailsDTO createUsage(CreateUsageDTO dto);
    UsageDetailsDTO updateUsage(Long id, CreateUsageDTO dto);
    void deleteUsage(Long id);

    // ✅ BL4 — get by titleId
    List<UsageDetailsDTO> getByTitleId(Long titleId);
}