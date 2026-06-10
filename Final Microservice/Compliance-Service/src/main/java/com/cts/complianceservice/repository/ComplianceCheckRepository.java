package com.cts.complianceservice.repository;

import com.cts.complianceservice.entity.ComplianceCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceCheckRepository extends JpaRepository<ComplianceCheck, Long> {
    List<ComplianceCheck> findByContractId(Long contractId);
    List<ComplianceCheck> findByScheduleId(Long scheduleId);
}