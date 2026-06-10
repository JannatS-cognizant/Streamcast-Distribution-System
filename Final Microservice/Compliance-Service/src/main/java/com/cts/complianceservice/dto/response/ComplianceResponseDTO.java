package com.cts.complianceservice.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ComplianceResponseDTO {
    private Long checkId;
    private Long contractId;
    private Long scheduleId;
    private String result;
    private String notes;
    private LocalDateTime checkedAt;
}