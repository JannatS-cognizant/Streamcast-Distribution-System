package com.Cts.service;
import com.Cts.ExceptionHandler.ResourceNotFoundException;
import com.Cts.entity.ComplianceCheck;
import com.Cts.repository.ComplianceCheckRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplianceServiceImpl implements ComplianceService {

    private final ComplianceCheckRepository repository;

    public ComplianceServiceImpl(ComplianceCheckRepository repository) {
        this.repository = repository;
    }

    @Override
    public ComplianceCheck runCheck(ComplianceCheck check) {
        check.setCheckedAt(LocalDateTime.now());
        return repository.save(check);
    }

    @Override
    public ComplianceCheck getCheckById(Long checkId) {
        return repository.findById(checkId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Compliance check not found with id: " + checkId));
    }

    @Override
    public List<ComplianceCheck> getAllChecks() {
        return repository.findAll();
    }

    
    
    @Override
    public ComplianceCheck updateCheck(Long checkId, ComplianceCheck updated) {

        ComplianceCheck existing = repository.findById(checkId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Compliance check not found with id: " + checkId
                )
            );

        existing.setResult(updated.getResult());
        existing.setNotes(updated.getNotes());

        return repository.save(existing);
    }
}
