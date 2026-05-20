package com.cts.usage_service.controller;

import com.cts.usage_service.api.ApiResponse;
import com.cts.usage_service.dto.CreateUsageDTO;
import com.cts.usage_service.dto.UsageBreakdownDTO;
import com.cts.usage_service.dto.UsageDetailsDTO;
import com.cts.usage_service.dto.UsageSummaryDTO;
import com.cts.usage_service.service.UsageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/usage")
@CrossOrigin(origins = "http://localhost:4200")
public class UsageController {

    private final UsageService service;

    public UsageController(UsageService service) {
        this.service = service;
    }

    // ✅ CREATE — operator manually enters usage data
    @PostMapping
    public ApiResponse<UsageDetailsDTO> createUsage(
            @Valid @RequestBody CreateUsageDTO dto
    ) {
        return new ApiResponse<>(
                true,
                "Usage record created successfully",
                service.createUsage(dto)
        );
    }

    // ✅ UPDATE (PUT) — operator corrects a wrong entry
    @PutMapping("/{id}")
    public ApiResponse<UsageDetailsDTO> updateUsage(
            @PathVariable Long id,
            @Valid @RequestBody CreateUsageDTO dto
    ) {
        return new ApiResponse<>(
                true,
                "Usage record updated successfully",
                service.updateUsage(id, dto)
        );
    }

    // ✅ DELETE — operator removes a wrong entry
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUsage(@PathVariable Long id) {
        service.deleteUsage(id);
        return new ApiResponse<>(
                true,
                "Usage record deleted successfully",
                "Deleted"
        );
    }

    // ✅ BL4 — Get all usage records for a specific title
    // Used by operators to report usage to licensors per title
    @GetMapping("/title/{titleId}")
    public ApiResponse<List<UsageDetailsDTO>> getByTitleId(
            @PathVariable Long titleId
    ) {
        return new ApiResponse<>(
                true,
                "Usage records fetched for titleId: " + titleId,
                service.getByTitleId(titleId)
        );
    }

    // ✅ SUMMARY — with date range validation
    @GetMapping("/summary")
    public ApiResponse<UsageSummaryDTO> getSummary(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return new ApiResponse<>(
                true,
                "Usage summary fetched successfully",
                service.getSummary(start, end)
        );
    }

    // ✅ DETAILS — with date range validation
    @GetMapping("/details")
    public ApiResponse<List<UsageDetailsDTO>> getDetails(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return new ApiResponse<>(
                true,
                "Usage details fetched successfully",
                service.getDetails(start, end)
        );
    }

    // ✅ BREAKDOWN — with date range validation
    @GetMapping("/breakdown")
    public ApiResponse<List<UsageBreakdownDTO>> getBreakdown(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam String groupBy
    ) {
        return new ApiResponse<>(
                true,
                "Usage breakdown fetched successfully",
                service.getBreakdown(start, end, groupBy)
        );
    }
}