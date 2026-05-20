package com.cts.complianceservice.service;

import com.cts.complianceservice.dto.request.ComplianceRequestDTO;
import com.cts.complianceservice.dto.request.ComplianceUpdateDTO;
import com.cts.complianceservice.dto.response.ComplianceResponseDTO;

import java.util.List;

public interface ComplianceService {
    ComplianceResponseDTO runCheck(ComplianceRequestDTO dto);
    ComplianceResponseDTO getCheckById(Long checkId);
    List<ComplianceResponseDTO> getAllChecks();
    ComplianceResponseDTO updateCheck(Long checkId, ComplianceUpdateDTO dto);
}