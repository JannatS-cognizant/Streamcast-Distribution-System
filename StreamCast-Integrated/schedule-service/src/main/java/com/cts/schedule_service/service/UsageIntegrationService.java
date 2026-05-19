package com.cts.schedule_service.service;

import com.cts.schedule_service.Feign.UsageBreakdownDTO;
import com.cts.schedule_service.Feign.UsageDetailsDTO;
import com.cts.schedule_service.Feign.UsageFeignClient;
import com.cts.schedule_service.Feign.UsageSummaryDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsageIntegrationService {

        private final UsageFeignClient client;

        public UsageIntegrationService(UsageFeignClient client) {
            this.client = client;
        }

        public UsageSummaryDTO fetchSummary(LocalDate start, LocalDate end) {
            return client.getSummary(start, end);
        }

        public List<UsageDetailsDTO> fetchDetails(LocalDate start, LocalDate end) {
            return client.getDetails(start, end);
        }

        public List<UsageBreakdownDTO> fetchBreakdown(
                LocalDate start,
                LocalDate end,
                String groupBy
        ) {
            return client.getBreakdown(start, end, groupBy);
        }
    }

