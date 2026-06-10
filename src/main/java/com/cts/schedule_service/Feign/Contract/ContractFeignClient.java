package com.cts.schedule_service.Feign.Contract;

import com.cts.schedule_service.Feign.Contract.ContractFeignFallback;
import com.cts.schedule_service.Feign.Contract.ContractResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "Contract-Service",
        fallback = ContractFeignFallback.class
)
public interface ContractFeignClient {

    @GetMapping("/contracts/{id}")
    ContractApiResponse getContractById(@PathVariable Long id);  // return wrapper
}