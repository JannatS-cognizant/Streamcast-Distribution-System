package org.example.clauseservice.controller;

import org.example.clauseservice.ContractClient;
import org.example.clauseservice.api.APIResponse;
import org.example.clauseservice.dto.request.ClauseRequestDTO;
import org.example.clauseservice.dto.response.ClauseResponseDTO;
import org.example.clauseservice.entity.Clause;
import org.example.clauseservice.mapper.request.ClauseRequestMapper;
import org.example.clauseservice.mapper.response.ClauseResponseMapper;
import org.example.clauseservice.service.ClauseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clauses")
public class ClauseController {

    @Autowired
    private ContractClient contractClient;

    @Autowired
    private ClauseService clauseService;

    @PreAuthorize("hasAnyRole('ADMIN','RIGHTS_MANAGER')")
    @PostMapping("/post")
    public APIResponse<ClauseResponseDTO> addClause(@RequestBody ClauseRequestDTO dto) {

        Clause clause = ClauseRequestMapper.toEntity(dto);
        Clause saved = clauseService.addClauses(clause);

        return new APIResponse<>("Clause created",
                ClauseResponseMapper.toDTO(saved), true);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RIGHTS_MANAGER','SCHEDULER','COMPLIANCE_OFFICER')")
    @GetMapping("/get")
    public APIResponse<List<ClauseResponseDTO>> getAllClauses() {

        List<ClauseResponseDTO> list = clauseService.getAllClauses()
                .stream()
                .map(ClauseResponseMapper::toDTO)
                .toList();

        return new APIResponse<>("All clauses fetched", list, true);
    }
}
