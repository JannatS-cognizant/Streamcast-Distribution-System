package com.cts.schedule_service.service;

import com.cts.schedule_service.Feign.Contract.ContractApiResponse;
import com.cts.schedule_service.Feign.Contract.ContractFeignClient;
import com.cts.schedule_service.Feign.Contract.ContractResponseDTO;
import com.cts.schedule_service.exception.InvalidScheduleException;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ContractIntegrationService {

    private final ContractFeignClient contractFeignClient;

    public ContractIntegrationService(ContractFeignClient contractFeignClient) {
        this.contractFeignClient = contractFeignClient;
    }

    @Retry(name = "contractService", fallbackMethod = "retryFallback")
    public ContractResponseDTO validateAndFetchContract(
            Long contractId,
            LocalDate scheduleStart,
            LocalDate scheduleEnd
    ) {
        // Feign now gets the wrapper, extract .getData()
        ContractApiResponse response = contractFeignClient.getContractById(contractId);

        if (response == null || response.getData() == null) {
            throw new InvalidScheduleException(
                    "Contract not found: " + contractId);
        }

        ContractResponseDTO contract = response.getData();  // ← extract actual contract

        // Parse String dates
        LocalDate contractStart = LocalDate.parse(contract.getStartDate());
        LocalDate contractEnd   = LocalDate.parse(contract.getEndDate());

        // Rule 1: Contract must be ACTIVE
        if (!"ACTIVE".equalsIgnoreCase(contract.getStatus())) {
            throw new InvalidScheduleException(
                    "Contract " + contractId + " is not ACTIVE. Status: " + contract.getStatus());
        }

        // Rule 2: Schedule start must be on or after contract start
        if (scheduleStart.isBefore(contractStart)) {
            throw new InvalidScheduleException(
                    "Schedule start " + scheduleStart + " is before contract start " + contractStart);
        }

        // Rule 3: Schedule end must be on or before contract end
        if (scheduleEnd.isAfter(contractEnd)) {
            throw new InvalidScheduleException(
                    "Schedule end " + scheduleEnd + " is after contract end " + contractEnd);
        }

        return contract;
    }

    // fallback — same params + Throwable
    public ContractResponseDTO retryFallback(
            Long contractId,
            LocalDate scheduleStart,
            LocalDate scheduleEnd,
            Throwable ex
    ) {
        throw new InvalidScheduleException(
                "Contract-Service unavailable after retries. Cannot process schedule.");
    }
}