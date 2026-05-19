package com.cts.complianceservice.mapper;

import com.cts.complianceservice.dto.request.ComplianceRequestDTO;
import com.cts.complianceservice.dto.response.ComplianceResponseDTO;
import com.cts.complianceservice.entity.ComplianceCheck;

public class ComplianceMapper {

    public static ComplianceCheck toEntity(ComplianceRequestDTO dto) {
        ComplianceCheck entity = new ComplianceCheck();
        entity.setContractId(dto.getContractId());
        entity.setScheduleId(dto.getScheduleId());
        entity.setResult(dto.getResult());
        entity.setNotes(dto.getNotes());
        return entity;
    }

    public static ComplianceResponseDTO toResponse(ComplianceCheck entity) {
        ComplianceResponseDTO dto = new ComplianceResponseDTO();
        dto.setCheckId(entity.getCheckId());
        dto.setContractId(entity.getContractId());
        dto.setScheduleId(entity.getScheduleId());
        dto.setResult(entity.getResult());
        dto.setNotes(entity.getNotes());
        dto.setCheckedAt(entity.getCheckedAt());
        return dto;
    }
}