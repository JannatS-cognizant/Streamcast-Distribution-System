package com.Cts.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClauseResponseDTO {
    private Long clauseId;
    private String clauseType;
    private String detailsJSON;
    private String effectiveFrom;
    private String effectiveTo;
    private Long contractId;
}
