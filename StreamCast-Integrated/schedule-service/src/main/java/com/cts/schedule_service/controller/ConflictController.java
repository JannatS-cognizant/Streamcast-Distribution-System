package com.cts.schedule_service.controller;

import com.cts.schedule_service.api.ApiResponse;
import com.cts.schedule_service.dto.ConflictDTO;
import com.cts.schedule_service.service.ConflictService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conflicts")
public class ConflictController {

    private final ConflictService service;

    public ConflictController(ConflictService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER','COMPLIANCE_OFFICER')")
    @GetMapping
    public ApiResponse<List<ConflictDTO>> getBySchedule(
            @RequestParam Long schedulesId
    ) {
        return new ApiResponse<>(
                true,
                "Conflicts fetched successfully",
                service.getConflictBySchedule(schedulesId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER','COMPLIANCE_OFFICER')")
    @GetMapping("/{id}")
    public ApiResponse<ConflictDTO> getConflictById(@PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Conflict fetched successfully",
                service.getConflictById(id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER')")
    @PutMapping("/{id}/resolve")
    public ApiResponse<ConflictDTO> resolve(@PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Conflict resolved successfully",
                service.resolveConflict(id)
        );
    }
}