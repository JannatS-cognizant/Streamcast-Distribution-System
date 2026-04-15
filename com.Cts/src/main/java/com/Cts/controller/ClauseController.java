package com.Cts.controller;

import com.Cts.api.APIResponse;
import com.Cts.dto.request.ClauseRequestDTO;
import com.Cts.dto.response.ClauseResponseDTO;
import com.Cts.entity.Clause;
import com.Cts.mapper.request.ClauseRequestMapper;
import com.Cts.mapper.response.ClauseResponseMapper;
import com.Cts.service.ClauseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clauses")
public class ClauseController {

    @Autowired
    private ClauseService clauseService;

    @PostMapping
    public APIResponse<ClauseResponseDTO> addClause(@RequestBody ClauseRequestDTO dto) {

        Clause clause = ClauseRequestMapper.toEntity(dto);
        Clause saved = clauseService.addClauses(clause);

        return new APIResponse<>("Clause created",
                ClauseResponseMapper.toDTO(saved), true);
    }

    @GetMapping
    public APIResponse<List<ClauseResponseDTO>> getAllClauses() {

        List<ClauseResponseDTO> list = clauseService.getAllClauses()
                .stream()
                .map(ClauseResponseMapper::toDTO)
                .toList();

        return new APIResponse<>("All clauses fetched", list, true);
    }
}
