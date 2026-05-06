package org.example.manifestservice.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Manifest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long manifestId;

    @NotBlank
    private String titleId;

    @NotBlank
    private String assetIdsJSON;

    @NotBlank
    private String destination;

    @NotBlank
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @Pattern(regexp = "CREATED|SENT|DELIVERED")
    private String status;

    private Long partnerId;
}