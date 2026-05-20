package com.cts.schedule_service.Feign.Title;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "catalog-service",
        fallback = CatalogFeignFallback.class
)
public interface CatalogFeignClient {

    // ✅ Business Logic 2: Fetch full title to check status
    // Replaces titleExists() — gives us both existence + status in one call
    @GetMapping("/api/titles/{id}")
    CatalogTitleDTO getTitle(@PathVariable Long id);
}