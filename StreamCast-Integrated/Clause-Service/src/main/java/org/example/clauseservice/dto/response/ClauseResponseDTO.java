package org.example.clauseservice.dto.response;

import lombok.Data;

@Data
public class ClauseResponseDTO {
    private Long clauseId;
    private String clauseType;
    private String detailsJSON;
    private String effectiveFrom;
    private String effectiveTo;
    private Long contractId;
}
