package com.Cts.mapper.request;

import com.Cts.dto.request.ClauseRequestDTO;
import com.Cts.entity.Clause;
import com.Cts.entity.Contract;

import java.time.LocalDate;

public class ClauseRequestMapper {
    public static Clause toEntity(ClauseRequestDTO dto) {

        Clause c = new Clause();

        c.setClauseType(dto.getClauseType());
        c.setDetailsJSON(dto.getDetailsJSON());
        c.setEffectiveFrom(LocalDate.parse(dto.getEffectiveFrom()));
        c.setEffectiveTo(LocalDate.parse(dto.getEffectiveTo()));

        if(dto.getContractId()!=null) {
            Contract contract = new Contract();
            contract.setContractId(dto.getContractId());

            c.setContract(contract);
        }

        return c;
    }
}
