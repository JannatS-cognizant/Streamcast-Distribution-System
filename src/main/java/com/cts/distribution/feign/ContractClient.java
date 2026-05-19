package com.cts.distribution.feign;

import com.cts.distribution.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "Contract-Service", configuration = FeignConfig.class)
public interface ContractClient {

    @GetMapping("/contracts/{id}")
    Object getContractById(@PathVariable("id") Long id);

    @GetMapping("/contracts")
    List<Object> getAllContracts();
}