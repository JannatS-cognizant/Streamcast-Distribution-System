package com.cts.schedule_service.Feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.schedule_service.config.FeignConfig;

@FeignClient(name = "catalog-service"
	, configuration = FeignConfig.class)
public interface TitleClient {


    @GetMapping("/api/titles/{id}/exists")
    Boolean isTitleExists(@PathVariable("id") Long titleId);

}
 