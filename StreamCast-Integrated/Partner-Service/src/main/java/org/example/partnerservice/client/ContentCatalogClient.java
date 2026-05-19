package org.example.partnerservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service")
public interface ContentCatalogClient {
    @GetMapping("/titles/{id}")
    Object getTitleById(@PathVariable int id);
}
