package com.cts.schedule_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "schedule_conflict")
public class Conflict {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long conflictId;

        @Column(nullable = false)
        private Long scheduleId1;

        @Column(nullable = false)
        private Long scheduleId2;

        @Column(nullable = false)
        private LocalDateTime detectedAt;

        @Column(nullable = false)
        private boolean resolved;
}





