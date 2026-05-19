package com.cts.usage_service.controller;

import com.cts.usage_service.api.ApiResponse;
import com.cts.usage_service.dto.UsageBreakdownDTO;
import com.cts.usage_service.dto.UsageDetailsDTO;
import com.cts.usage_service.dto.UsageSummaryDTO;
import com.cts.usage_service.service.UsageService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/usage")
public class UsageController {

    private final UsageService service;

    public UsageController(UsageService service) {
        this.service = service;
    }

    // ✅ SUMMARY
    @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE_OFFICER','SCHEDULER')")
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

    // ✅ DETAILS
    @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE_OFFICER','SCHEDULER')")
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

    // ✅ BREAKDOWN
    @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE_OFFICER','SCHEDULER')")
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


