package com.cts.usage_service.service;

import com.cts.usage_service.dto.UsageSummaryDTO;
import com.cts.usage_service.dto.UsageDetailsDTO;
import com.cts.usage_service.dto.UsageBreakdownDTO;
import com.cts.usage_service.entity.UsageRecord;
import com.cts.usage_service.mapper.UsageResponseMapper;
import com.cts.usage_service.repository.UsageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsageServiceImplementation implements UsageService {

    private final UsageRepository repo;

    public UsageServiceImplementation(UsageRepository repo) {
        this.repo = repo;
    }

    // ✅ SUMMARY
    @Override
    public UsageSummaryDTO getSummary(LocalDate start, LocalDate end) {

        UsageSummaryDTO dto = new UsageSummaryDTO();
        dto.totalViews = repo.sumViews(start, end);
        dto.totalRevenue = repo.sumRevenue(start, end);
        dto.totalRecords = repo.countRecords(start, end);

        return dto;
    }

    // ✅ DETAILS
    @Override
    public List<UsageDetailsDTO> getDetails(LocalDate start, LocalDate end) {

        List<UsageRecord> records = repo.findByDateBetween(start, end);

        return records.stream()
                .map(UsageResponseMapper::toDetailsDTO)
                .toList();
    }

    // ✅ BREAKDOWN
    @Override
    public List<UsageBreakdownDTO> getBreakdown(
            LocalDate start,
            LocalDate end,
            String groupBy
    ) {
        return switch (groupBy.toLowerCase()) {
            case "platform" -> repo.groupByPlatform(start, end);
            case "title" -> repo.groupByTitle(start, end);
            case "date" -> repo.groupByDate(start, end);
            default -> throw new IllegalArgumentException("Invalid groupBy value");
        };
    }
}