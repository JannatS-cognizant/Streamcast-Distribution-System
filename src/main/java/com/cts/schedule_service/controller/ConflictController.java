package com.cts.schedule_service.controller;

import com.cts.schedule_service.api.ApiResponse;
import com.cts.schedule_service.dto.ConflictDTO;
import com.cts.schedule_service.service.ConflictService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conflicts")
@CrossOrigin(origins = "http://localhost:4200")
public class ConflictController {

    private final ConflictService service;

    public ConflictController(ConflictService service) {
        this.service = service;
    }

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

    @GetMapping("/{id}")
    public ApiResponse<ConflictDTO> getConflictById(@PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Conflict fetched successfully",
                service.getConflictById(id)
        );
    }

    @PutMapping("/{id}/resolve")
    public ApiResponse<ConflictDTO> resolve(@PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Conflict resolved successfully",
                service.resolveConflict(id)
        );
    }
}