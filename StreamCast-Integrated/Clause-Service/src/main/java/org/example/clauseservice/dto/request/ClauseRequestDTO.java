package org.example.clauseservice.dto.request;

import lombok.Data;

@Data
public class ClauseRequestDTO {

    private String clauseType;
    private String detailsJSON;
    private String effectiveFrom;
    private String effectiveTo;
    private Long contractId;
}


