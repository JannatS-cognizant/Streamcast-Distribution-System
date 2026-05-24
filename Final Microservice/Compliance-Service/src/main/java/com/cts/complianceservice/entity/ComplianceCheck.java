package com.cts.complianceservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_checks")
@Data
public class ComplianceCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkId;

    // References Contract from Contract-Service (no cross-service FK)
    @Column(nullable = false)
    private Long contractId;

    // References Schedule from schedule-service (no cross-service FK)
    @Column(nullable = false)
    private Long scheduleId;

    // e.g. "PASS" / "FAIL" / "PENDING"
    @Column(nullable = false)
    private String result;

    @Column(nullable = false, length = 1000)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime checkedAt;
}