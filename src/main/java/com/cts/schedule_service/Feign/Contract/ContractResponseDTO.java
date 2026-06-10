package com.cts.schedule_service.Feign.Contract;

import lombok.Data;

@Data
public class ContractResponseDTO {
        private Long contractId;
        private String titleId;
        private String territoryListJson;
        private String startDate;       // String — Afrin's mapper sends "2024-01-01"
        private String endDate;
        private Boolean exclusivityFlag;
        private String termsSummary;
        private String status;          // "ACTIVE" or "INACTIVE"
}