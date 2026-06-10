package com.cts.complianceservice.service.impl;

import com.cts.complianceservice.dto.request.ComplianceRequestDTO;
import com.cts.complianceservice.dto.request.ComplianceUpdateDTO;
import com.cts.complianceservice.dto.response.ComplianceResponseDTO;
import com.cts.complianceservice.entity.ComplianceCheck;
import com.cts.complianceservice.exception.ResourceNotFoundException;
import com.cts.complianceservice.mapper.ComplianceMapper;
import com.cts.complianceservice.repository.ComplianceCheckRepository;
import com.cts.complianceservice.service.ComplianceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplianceServiceImpl implements ComplianceService {

    private final ComplianceCheckRepository repository;

    public ComplianceServiceImpl(ComplianceCheckRepository repository) {
        this.repository = repository;
    }

    @Override
    public ComplianceResponseDTO runCheck(ComplianceRequestDTO dto) {
        ComplianceCheck entity = ComplianceMapper.toEntity(dto);
        entity.setCheckedAt(LocalDateTime.now());
        return ComplianceMapper.toResponse(repository.save(entity));
    }

    @Override
    public ComplianceResponseDTO getCheckById(Long checkId) {
        ComplianceCheck check = repository.findById(checkId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compliance check not found with id: " + checkId));
        return ComplianceMapper.toResponse(check);
    }

    @Override
    public List<ComplianceResponseDTO> getAllChecks() {
        return repository.findAll()
                .stream()
                .map(ComplianceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ComplianceResponseDTO updateCheck(Long checkId, ComplianceUpdateDTO dto) {
        ComplianceCheck existing = repository.findById(checkId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compliance check not found with id: " + checkId));

        existing.setResult(dto.getResult());
        existing.setNotes(dto.getNotes());

        return ComplianceMapper.toResponse(repository.save(existing));
    }
}