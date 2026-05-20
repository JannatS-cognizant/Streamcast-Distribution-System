package org.example.clauseservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClauseRequestDTO {

    private String clauseType;
    private String detailsJSON;
    private String effectiveFrom;
    private String effectiveTo;
    private Long contractId;
}


