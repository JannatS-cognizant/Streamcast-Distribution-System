package org.example.contractservice.dto.response;

import lombok.Data;

@Data
public class ContractResponseDTO {
    private Long contractId;
    private String titleId;
    private String territoryListJson;
    private String startDate;
    private String endDate;
    private Boolean exclusivityFlag;
    private String termsSummary;
    private String status;
}
