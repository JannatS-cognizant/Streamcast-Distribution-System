package com.cts.usage_service.service;

import com.cts.usage_service.dto.CreateUsageDTO;
import com.cts.usage_service.dto.UsageBreakdownDTO;
import com.cts.usage_service.dto.UsageDetailsDTO;
import com.cts.usage_service.dto.UsageSummaryDTO;
import com.cts.usage_service.entity.UsageRecord;
import com.cts.usage_service.exception.InvalidUsageException;
import com.cts.usage_service.exception.ResourceNotFoundException;
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

    // ✅ CREATE — operator manually enters usage data
    @Override
    public UsageDetailsDTO createUsage(CreateUsageDTO dto) {

        // BL5 — Revenue consistency check
        validateRevenueConsistency(dto.views, dto.revenue);

        // BL3 — Date must not be in future
        if (dto.date.isAfter(LocalDate.now())) {
            throw new InvalidUsageException(
                    "Usage date cannot be in the future");
        }

        // BL3 — Prevent duplicate entry for same title+platform+date
        if (repo.existsByTitleIdAndPlatformAndDate(
                dto.titleId, dto.platform, dto.date)) {
            throw new InvalidUsageException(
                    "Usage record already exists for titleId=" + dto.titleId
                            + ", platform=" + dto.platform
                            + ", date=" + dto.date);
        }

        UsageRecord record = new UsageRecord();
        record.setTitleId(dto.titleId);
        record.setPlatform(dto.platform.trim());   // BL8 — normalize platform
        record.setDate(dto.date);
        record.setViews(dto.views);
        record.setRevenue(dto.revenue);

        return UsageResponseMapper.toDetailsDTO(repo.save(record));
    }

    // ✅ UPDATE (PUT) — operator corrects a wrong entry
    @Override
    public UsageDetailsDTO updateUsage(Long id, CreateUsageDTO dto) {

        if (id <= 0) {
            throw new InvalidUsageException("Invalid usage record ID");
        }

        UsageRecord existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usage record not found with ID: " + id));

        // BL5 — Revenue consistency check
        validateRevenueConsistency(dto.views, dto.revenue);

        // BL3 — Date must not be in future
        if (dto.date.isAfter(LocalDate.now())) {
            throw new InvalidUsageException(
                    "Usage date cannot be in the future");
        }

        existing.setTitleId(dto.titleId);
        existing.setPlatform(dto.platform.trim());   // BL8 — normalize platform
        existing.setDate(dto.date);
        existing.setViews(dto.views);
        existing.setRevenue(dto.revenue);

        return UsageResponseMapper.toDetailsDTO(repo.save(existing));
    }

    // ✅ DELETE — operator removes a wrong entry
    @Override
    public void deleteUsage(Long id) {

        if (id <= 0) {
            throw new InvalidUsageException("Invalid usage record ID");
        }

        UsageRecord record = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usage record not found with ID: " + id));

        repo.delete(record);
    }

    // ✅ BL4 — Get all usage records for a specific title
    @Override
    public List<UsageDetailsDTO> getByTitleId(Long titleId) {

        if (titleId <= 0) {
            throw new InvalidUsageException("Invalid titleId");
        }

        List<UsageRecord> records = repo.findByTitleId(titleId);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No usage records found for titleId: " + titleId);
        }

        return records.stream()
                .map(UsageResponseMapper::toDetailsDTO)
                .toList();
    }

    // ✅ SUMMARY — BL3: validate date range
    @Override
    public UsageSummaryDTO getSummary(LocalDate start, LocalDate end) {

        validateDateRange(start, end);  // BL3

        UsageSummaryDTO dto = new UsageSummaryDTO();
        dto.totalViews   = repo.sumViews(start, end);
        dto.totalRevenue = repo.sumRevenue(start, end);
        dto.totalRecords = repo.countRecords(start, end);

        return dto;
    }

    // ✅ DETAILS — BL3: validate date range
    @Override
    public List<UsageDetailsDTO> getDetails(LocalDate start, LocalDate end) {

        validateDateRange(start, end);  // BL3

        return repo.findByDateBetween(start, end)
                .stream()
                .map(UsageResponseMapper::toDetailsDTO)
                .toList();
    }

    // ✅ BREAKDOWN — BL3: validate date range
    @Override
    public List<UsageBreakdownDTO> getBreakdown(
            LocalDate start, LocalDate end, String groupBy) {

        validateDateRange(start, end);  // BL3

        return switch (groupBy.toLowerCase()) {
            case "platform" -> repo.groupByPlatform(start, end);
            case "title"    -> repo.groupByTitle(start, end);
            case "date"     -> repo.groupByDate(start, end);
            default -> throw new InvalidUsageException(
                    "Invalid groupBy value. Use: platform, title, or date");
        };
    }

    // ✅ BL5 — Revenue consistency validator — reusable
    private void validateRevenueConsistency(Long views, Double revenue) {
        if (views == 0 && revenue > 0) {
            throw new InvalidUsageException(
                    "Revenue cannot be greater than 0 when views is 0");
        }
    }

    // ✅ BL3 — Date range validator — reusable
    private void validateDateRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new InvalidUsageException(
                    "Start date must be before or equal to end date");
        }
    }
}