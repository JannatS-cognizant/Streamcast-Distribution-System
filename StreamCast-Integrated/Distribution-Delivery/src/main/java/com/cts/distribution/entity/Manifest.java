package com.cts.distribution.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="manifest")
public class Manifest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String titleId;
    private String assetIdsJSON;
    private String destination;
    private String createdBy;
    private LocalDateTime createdAt;
    private String status;
    private Long partnerId;
	
}
