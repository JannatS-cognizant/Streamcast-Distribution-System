package com.Cts.mapper.response;

import com.Cts.dto.response.ClauseResponseDTO;
import com.Cts.entity.Clause;

public class ClauseResponseMapper {
    public static ClauseResponseDTO toDTO(Clause c) {

        ClauseResponseDTO dto = new ClauseResponseDTO();

        dto.setClauseId(c.getClauseId());
        dto.setClauseType(c.getClauseType());
        dto.setDetailsJSON(c.getDetailsJSON());
        dto.setEffectiveFrom(String.valueOf(c.getEffectiveFrom()));
        dto.setEffectiveTo(String.valueOf(c.getEffectiveTo()));
        dto.setContractId(c.getContract()!=null?c.getContract().getContractId():null);

        return dto;
    }
}
