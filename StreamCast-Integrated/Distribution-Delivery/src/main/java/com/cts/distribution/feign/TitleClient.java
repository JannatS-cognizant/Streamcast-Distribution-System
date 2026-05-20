package com.cts.distribution.feign;

import com.cts.distribution.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", configuration = FeignConfig.class)
public interface TitleClient {

    @GetMapping("/api/titles/{id}/exists")
    Boolean isTitleExists(@PathVariable("id") Long titleId);
}