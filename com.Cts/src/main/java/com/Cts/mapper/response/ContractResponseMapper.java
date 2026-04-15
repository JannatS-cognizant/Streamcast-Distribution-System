package com.Cts.mapper.response;

import com.Cts.dto.response.ContractResponseDTO;
import com.Cts.entity.Contract;

public class ContractResponseMapper {
    public static ContractResponseDTO toDTO(Contract c) {

        ContractResponseDTO dto = new ContractResponseDTO();

        dto.setContractId(c.getContractId());
        dto.setTitleId(c.getTitleId());
        dto.setTerritoryListJson(c.getTerritoryListJson());
        dto.setStartDate(c.getStartDate().toString());
        dto.setEndDate(c.getEndDate().toString());
        dto.setExclusivityFlag(c.getExclusivityFlag());
        dto.setTermsSummary(c.getTermsSummary());
        dto.setStatus(c.getStatus());

        return dto;
    }
}
