package com.cts.schedule_service.Feign.usage;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(
        name = "usage-service"   // removed url = "http://localhost:8081" — Eureka resolves it
)
public interface UsageFeignClient {

    @GetMapping("/api/usage/summary")
    UsageSummaryDTO getSummary(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    );

    @GetMapping("/api/usage/details")
    List<UsageDetailsDTO> getDetails(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    );

    @GetMapping("/api/usage/breakdown")
    List<UsageBreakdownDTO> getBreakdown(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam String groupBy
    );
}