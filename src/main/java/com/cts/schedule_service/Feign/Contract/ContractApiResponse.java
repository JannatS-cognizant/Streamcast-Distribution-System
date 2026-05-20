package com.cts.schedule_service.Feign.Contract;

import lombok.Data;

@Data
public class ContractApiResponse {
    private String message;
    private ContractResponseDTO data;    // actual contract is here
    private Boolean success;
}
