package com.Cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Cts.entity.ComplianceCheck;

public interface ComplianceCheckRepository extends JpaRepository<ComplianceCheck, Long> {
}
