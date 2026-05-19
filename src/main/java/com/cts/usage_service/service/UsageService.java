package com.cts.usage_service.service;

import com.cts.usage_service.dto.UsageSummaryDTO;
import com.cts.usage_service.dto.UsageDetailsDTO;
import com.cts.usage_service.dto.UsageBreakdownDTO;

import java.time.LocalDate;
import java.util.List;

public interface UsageService {


    UsageSummaryDTO getSummary(LocalDate start, LocalDate end);


    List<UsageDetailsDTO> getDetails(LocalDate start, LocalDate end);


    List<UsageBreakdownDTO> getBreakdown(
            LocalDate start,
            LocalDate end,
            String groupBy
    );
}


