package com.cts.complianceservice.controller;

import com.cts.complianceservice.api.APIResponse;
import com.cts.complianceservice.dto.request.ComplianceRequestDTO;
import com.cts.complianceservice.dto.request.ComplianceUpdateDTO;
import com.cts.complianceservice.dto.response.ComplianceResponseDTO;
import com.cts.complianceservice.service.ComplianceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compliance")
@CrossOrigin
public class ComplianceController {

    private final ComplianceService complianceService;

    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    /**
     * POST /compliance/check
     * Runs a new compliance check for a given contractId + scheduleId.
     * Project table: "Compliance Dashboard API" — POST /compliance/check
     */
    @PostMapping("/check")
    public ResponseEntity<APIResponse<ComplianceResponseDTO>> runComplianceCheck(
            @Valid @RequestBody ComplianceRequestDTO requestDTO) {
        ComplianceResponseDTO result = complianceService.runCheck(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success("Compliance check completed", result));
    }

    /**
     * GET /compliance/check/{checkId}
     * Project table: "Compliance Results Screen API" — GET /compliance/check/{checkId}
     */
    @GetMapping("/check/{checkId}")
    public ResponseEntity<APIResponse<ComplianceResponseDTO>> getCheckById(
            @PathVariable Long checkId) {
        return ResponseEntity.ok(
                APIResponse.success("Compliance check retrieved",
                        complianceService.getCheckById(checkId)));
    }

    /**
     * GET /compliance/checks
     * Project table: GET /compliance/checks
     */
    @GetMapping("/checks")
    public ResponseEntity<APIResponse<List<ComplianceResponseDTO>>> getAllChecks() {
        return ResponseEntity.ok(
                APIResponse.success("All compliance checks retrieved",
                        complianceService.getAllChecks()));
    }

    /**
     * PUT /compliance/checks/{checkId}
     * Project table: PUT /compliance/checks/{checkId}
     * Used by Legal Officers to update result/notes after manual review.
     */
    @PutMapping("/checks/{checkId}")
    public ResponseEntity<APIResponse<ComplianceResponseDTO>> updateCheck(
            @PathVariable Long checkId,
            @Valid @RequestBody ComplianceUpdateDTO dto) {
        return ResponseEntity.ok(
                APIResponse.success("Compliance check updated",
                        complianceService.updateCheck(checkId, dto)));
    }
}