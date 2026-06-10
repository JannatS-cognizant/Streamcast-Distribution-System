package com.cts.schedule_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name="schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    private Long titleId;
    private Long contractId;
    private String platform;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    @Column(name = "window_type")
    private String windowtype;
    private String status;


}

