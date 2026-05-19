package com.cts.distribution.entity;
 
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
 
@Data
@Entity
@Table(name="receipt")
public class Receipt {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptId;
 
    private Long manifestId;
    private LocalDateTime receivedAt;
    private String receivedBy;
    private String receiptURI;
    private String status;
}