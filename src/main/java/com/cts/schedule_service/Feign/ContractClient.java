package com.cts.schedule_service.Feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.cts.schedule_service.config.FeignConfig;

import java.time.LocalDateTime;

@FeignClient(name = "contract-service"
		,configuration = FeignConfig.class )
    public interface ContractClient {

        @GetMapping("/api/contracts/{id}")
        boolean isContractValid(
                @PathVariable("id") Long contractId,
                @RequestParam("start") LocalDateTime start,
                @RequestParam("end") LocalDateTime end
        );

    }
 