package com.Cts.mapper.request;

import com.Cts.dto.request.ContractRequestDTO;
import com.Cts.entity.Contract;

import java.time.LocalDate;

public class ContractRequestMapper {
    public static Contract toEntity(ContractRequestDTO dto) {

        Contract c = new Contract();

        c.setTitleId(dto.getTitleId());
        c.setTerritoryListJson(dto.getTerritoryListJson());
        c.setStartDate(LocalDate.parse(dto.getStartDate()));
        c.setEndDate(LocalDate.parse(dto.getEndDate()));
        c.setExclusivityFlag(dto.getExclusivityFlag());
        c.setTermsSummary(dto.getTermsSummary());
        c.setStatus(dto.getStatus());

        return c;
    }
}

