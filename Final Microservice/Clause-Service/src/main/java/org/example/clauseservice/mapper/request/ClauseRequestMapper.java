package org.example.clauseservice.mapper.request;

import org.example.clauseservice.dto.request.ClauseRequestDTO;
import org.example.clauseservice.entity.Clause;

import java.time.LocalDate;

public class ClauseRequestMapper {

    public static Clause toEntity(ClauseRequestDTO dto) {

        Clause c = new Clause();

        c.setClauseType(dto.getClauseType());
        c.setDetailsJSON(dto.getDetailsJSON());
        c.setEffectiveFrom(LocalDate.parse(dto.getEffectiveFrom()));
        c.setEffectiveTo(LocalDate.parse(dto.getEffectiveTo()));

        c.setContractId(dto.getContractId()); // ✅ FIXED

        return c;
    }
}