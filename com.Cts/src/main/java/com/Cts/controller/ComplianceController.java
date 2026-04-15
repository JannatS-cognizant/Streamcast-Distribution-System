package com.Cts.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import com.Cts.dto.request.ComplianceRequestDTO;
import com.Cts.dto.response.ComplianceResponseDTO;
import com.Cts.mapper.request.ComplianceMapper;
import com.Cts.entity.ComplianceCheck;
import com.Cts.service.ComplianceService;

@RestController
@RequestMapping("/compliance")
public class ComplianceController {

    private final ComplianceService complianceService;

    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @PostMapping("/check")
    public ComplianceResponseDTO runComplianceCheck(
            @Valid @RequestBody ComplianceRequestDTO requestDTO) {

        ComplianceCheck entity =
                ComplianceMapper.toEntity(requestDTO);

        ComplianceCheck saved =
                complianceService.runCheck(entity);

        return ComplianceMapper.toResponse(saved);
    }
}