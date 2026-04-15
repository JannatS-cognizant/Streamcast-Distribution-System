package com.Cts.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Manifest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long manifestId;

    @NotBlank(message = "Title Id is required")
    private String titleId;

    @NotBlank(message = "Assest Id is required")
    private String assetIdsJSON;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotBlank(message = "CreatedBy is required")
    private String createdBy;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "CREATED|SENT|DELIVERED",message = "Invalid Status")
    private String status;


    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;
}
