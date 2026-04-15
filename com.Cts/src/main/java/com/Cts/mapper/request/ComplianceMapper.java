package com.Cts.mapper.request;

import com.Cts.dto.request.ComplianceRequestDTO;
import com.Cts.dto.response.ComplianceResponseDTO;
import com.Cts.entity.ComplianceCheck;

public class ComplianceMapper {

    // Convert Request DTO to Entity
    public static ComplianceCheck toEntity(ComplianceRequestDTO dto) {
        ComplianceCheck entity = new ComplianceCheck();

        entity.setContractId(dto.getContractId());
        entity.setScheduleId(dto.getScheduleId());
        entity.setResult(dto.getResult());
        entity.setNotes(dto.getNotes());

        return entity;
    }

    // Convert Entity to Response DTO
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
