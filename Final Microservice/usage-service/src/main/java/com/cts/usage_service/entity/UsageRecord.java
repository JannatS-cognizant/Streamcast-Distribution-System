package com.cts.usage_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "usage_record")
public class UsageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usageId;

    @Column(name = "title_id")
    private Long titleId;
    private String platform;
    private LocalDate date;
    private Long views;
    private Double revenue;
}
