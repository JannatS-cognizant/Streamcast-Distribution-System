package com.cts.schedule_service.controller;

import com.cts.schedule_service.api.ApiResponse;
import com.cts.schedule_service.dto.CalendarScheduleDTO;
import com.cts.schedule_service.dto.CreateScheduleDTO;
import com.cts.schedule_service.service.ConflictService;
import com.cts.schedule_service.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService service;
    private final ConflictService conflictService;

    public ScheduleController(
            ScheduleService service,
            ConflictService conflictService
    ) {
        this.service = service;
        this.conflictService = conflictService;
    }

    // ✅ Calendar view
    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER','RIGHTS_MANAGER','COMPLIANCE_OFFICER')")
    @GetMapping("/calendar")
    public ApiResponse<List<CalendarScheduleDTO>> getCalendar(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String platform,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return new ApiResponse<>(
                true,
                "Schedules fetched successfully",
                service.getCalendar(start, end, status, platform, page, size)
        );
    }

    // ✅ Create schedule
    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER')")
    @PostMapping
    public ApiResponse<CalendarScheduleDTO> create(
            @Valid @RequestBody CreateScheduleDTO dto
    ) {
        return new ApiResponse<>(
                true,
                "Schedule created successfully",
                service.createSchedule(dto)
        );
    }

    // ✅ Get schedule by ID
    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER','RIGHTS_MANAGER')")
    @GetMapping("/{id}")
    public ApiResponse<CalendarScheduleDTO> getById(@PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Schedule fetched successfully",
                service.getById(id)
        );
    }

    // ✅ Update schedule
    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER')")
    @PutMapping("/{id}")
    public ApiResponse<CalendarScheduleDTO> update(
            @PathVariable Long id,
            @RequestBody CreateScheduleDTO dto
    ) {
        return new ApiResponse<>(
                true,
                "Schedule updated successfully",
                service.updateSchedule(id, dto)
        );
    }

    // Partial update
    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER')")
    @PatchMapping("/{id}")
    public ApiResponse<CalendarScheduleDTO> partialUpdate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates
    ) {
        return new ApiResponse<>(
                true,
                "Schedule updated successfully",
                service.partialUpdate(id, updates)
        );
    }


    // ✅ Delete schedule
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        service.deleteSchedule(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Schedule deleted successfully", null)
        );
    }

    // ✅ Detect conflicts
    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER')")
    @PostMapping("/{id}/detect-conflicts")
    public ApiResponse<String> detectConflicts(@PathVariable Long id) {
        conflictService.detectConflicts(id);
        return new ApiResponse<>(
                true,
                "Conflict detection completed",
                null
        );
    }
}
