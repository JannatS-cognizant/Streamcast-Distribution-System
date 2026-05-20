package com.cts.distribution.entity;
 
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
 
@Data
@Entity
@Table(name="delivery_attempt")
public class DeliveryAttempt {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;
 
    private Long manifestId;
    private LocalDateTime attemptedAt;
    private String methodNote;
    private String result;
    private String details;
    private int retries;
}