package com.Cts.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClauseRequestDTO {

    private String clauseType;
    private String detailsJSON;
    private String effectiveFrom;
    private String effectiveTo;
    private Long contractId;
}


