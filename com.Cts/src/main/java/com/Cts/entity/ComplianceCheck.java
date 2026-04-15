package com.Cts.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_check")
@Data
public class ComplianceCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkId;

    @Column(nullable = false)
    private Long contractId;

    @Column(nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    private String result;

    @Column(nullable = false)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime checkedAt;
}