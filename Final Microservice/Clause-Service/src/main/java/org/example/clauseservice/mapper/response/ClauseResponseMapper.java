package org.example.clauseservice.mapper.response;

import org.example.clauseservice.dto.response.ClauseResponseDTO;
import org.example.clauseservice.entity.Clause;

public class ClauseResponseMapper {

    public static ClauseResponseDTO toDTO(Clause c) {

        ClauseResponseDTO dto = new ClauseResponseDTO();

        dto.setClauseId(c.getClauseId());
        dto.setClauseType(c.getClauseType());
        dto.setDetailsJSON(c.getDetailsJSON());
        dto.setEffectiveFrom(c.getEffectiveFrom().toString());
        dto.setEffectiveTo(c.getEffectiveTo().toString());
        dto.setContractId(c.getContractId());

        return dto;
    }
}
