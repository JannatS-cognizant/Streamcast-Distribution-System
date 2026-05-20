package com.cts.schedule_service.Feign.Contract;

import com.cts.schedule_service.Feign.Contract.ContractResponseDTO;
import com.cts.schedule_service.Feign.Contract.ContractFeignClient;
import com.cts.schedule_service.exception.InvalidScheduleException;
import org.springframework.stereotype.Component;

@Component
public class ContractFeignFallback implements ContractFeignClient {

    @Override
    public ContractApiResponse getContractById(Long id) {
        throw new InvalidScheduleException(
                "Contract-Service is unavailable. Cannot validate contract: " + id
        );
    }
}