package org.example.contractservice.mapper.response;

import org.example.contractservice.dto.response.ContractResponseDTO;
import org.example.contractservice.entity.Contract;

public class ContractResponseMapper {
    public static ContractResponseDTO toDTO(Contract c) {

        ContractResponseDTO dto = new ContractResponseDTO();

        dto.setContractId(c.getContractId());
        dto.setTitleId(String.valueOf(c.getTitleId()));
        dto.setTerritoryListJson(c.getTerritoryListJson());
        dto.setStartDate(c.getStartDate().toString());
        dto.setEndDate(c.getEndDate().toString());
        dto.setExclusivityFlag(c.getExclusivityFlag());
        dto.setTermsSummary(c.getTermsSummary());
        dto.setStatus(c.getStatus());

        return dto;
    }
}
