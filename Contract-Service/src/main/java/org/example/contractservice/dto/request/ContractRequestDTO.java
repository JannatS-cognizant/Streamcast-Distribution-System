package org.example.contractservice.dto.request;
import lombok.Data;

@Data
public class ContractRequestDTO {

    private String titleId;
    private String territoryListJson;
    private String startDate;
    private String endDate;
    private Boolean exclusivityFlag;
    private String termsSummary;
    private String status;

}
