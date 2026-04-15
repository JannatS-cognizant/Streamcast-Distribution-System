package com.Cts.dto.request;

import com.Cts.entity.Partner;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AccessLogRequestDTO {
    private String accessedAt;
    private String action;
}
