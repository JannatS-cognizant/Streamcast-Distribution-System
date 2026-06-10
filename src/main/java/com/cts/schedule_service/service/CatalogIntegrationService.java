package com.cts.schedule_service.service;

import com.cts.schedule_service.Feign.Title.CatalogFeignClient;
import com.cts.schedule_service.Feign.Title.CatalogTitleDTO;
import com.cts.schedule_service.exception.InvalidScheduleException;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class CatalogIntegrationService {

    private final CatalogFeignClient catalogFeignClient;

    public CatalogIntegrationService(CatalogFeignClient catalogFeignClient) {
        this.catalogFeignClient = catalogFeignClient;
    }

    // ✅ Business Logic 2: Validates title exists AND is ACTIVE
    @Retry(name = "catalogService", fallbackMethod = "retryFallback")
    public void validateTitle(Long titleId) {

        // Fetch full title — gives us both existence + status
        CatalogTitleDTO title = catalogFeignClient.getTitle(titleId);

        // Rule 1: Title must exist
        if (title == null) {
            throw new InvalidScheduleException(
                    "Title not found in catalog. Invalid titleId: " + titleId);
        }

        // Rule 2: Title must be ACTIVE — cannot schedule inactive content
        if (!"ACTIVE".equalsIgnoreCase(title.getStatus())) {
            throw new InvalidScheduleException(
                    "Title " + titleId + " is not ACTIVE. Status: " + title.getStatus()
                            + ". Cannot create schedule for inactive title.");
        }
    }

    // Called after all 3 retries fail
    public void retryFallback(Long titleId, Throwable ex) {
        throw new InvalidScheduleException(
                "Catalog-Service unavailable after retries. Cannot validate titleId: " + titleId);
    }
}