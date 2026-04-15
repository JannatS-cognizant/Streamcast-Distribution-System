package com.Cts.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long logId;

    @NotNull(message = "Accessed Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate accessedAt;

    @NotBlank(message = "Action is required")
    @Size(min = 3,max = 100)
    private String action;


    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;


    @ManyToOne
    @JoinColumn(name = "manifest_id")
    private Manifest manifest;
}
