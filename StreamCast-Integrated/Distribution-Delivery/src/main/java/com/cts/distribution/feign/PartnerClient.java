package com.cts.distribution.feign;

import com.cts.distribution.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "Partner-Service", configuration = FeignConfig.class)
public interface PartnerClient {

    @GetMapping("/partners/{id}")
    Object getPartnerById(@PathVariable("id") Long id);

    @GetMapping("/partners")
    List<Object> getAllPartners();
}