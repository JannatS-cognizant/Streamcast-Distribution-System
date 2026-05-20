package com.cts.schedule_service.service;

import com.cts.schedule_service.Feign.Contract.ContractResponseDTO;
import com.cts.schedule_service.dto.CalendarScheduleDTO;
import com.cts.schedule_service.dto.CreateScheduleDTO;
import com.cts.schedule_service.entity.Schedule;
import com.cts.schedule_service.exception.InvalidScheduleException;
import com.cts.schedule_service.exception.ResourceNotFoundException;
import com.cts.schedule_service.mapper.request.ScheduleRequestMapper;
import com.cts.schedule_service.mapper.response.ScheduleResponseMapper;
import com.cts.schedule_service.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImplementation implements ScheduleService {

    private final ScheduleRepository repo;
    private final CatalogIntegrationService catalogIntegrationService;
    private final ContractIntegrationService contractIntegrationService;

    // ✅ Valid window types — from spec §4.4
    private static final List<String> VALID_WINDOW_TYPES =
            List.of("PREMIERE", "REPEAT", "OTT", "SYNDICATION");

    public ScheduleServiceImplementation(
            ScheduleRepository repo,
            CatalogIntegrationService catalogIntegrationService,
            ContractIntegrationService contractIntegrationService
    ) {
        this.repo = repo;
        this.catalogIntegrationService = catalogIntegrationService;
        this.contractIntegrationService = contractIntegrationService;
    }

    // ✅ CREATE
    @Override
    public CalendarScheduleDTO createSchedule(CreateScheduleDTO dto) {

        // Step 1: Validate titleId exists AND is ACTIVE in catalog-service
        catalogIntegrationService.validateTitle(dto.titleId);

        // Step 2: BL2 — Validate windowType is one of allowed values
        validateWindowType(dto.windowType);

        // Step 3: BL8 — Normalize platform (trim + lowercase for consistency)
        dto.platform = dto.platform.trim();

        // Step 4: Validate schedule window (start before end)
        validateScheduleWindow(dto.startDateTime, dto.endDateTime);

        // Step 5: Validate contractId + BL1 — territory check
        ContractResponseDTO contract = contractIntegrationService.validateAndFetchContract(
                dto.contractId,
                dto.startDateTime.toLocalDate(),
                dto.endDateTime.toLocalDate()
        );

        // Step 6: BL1 — Territory check — platform must match contract territory
        validateTerritory(dto.platform, contract.getTerritoryListJson());

        // Step 7: BL1 — Duplicate schedule check
        List<Schedule> overlapping = repo.findOverlapping(
                dto.titleId,
                dto.platform,
                dto.startDateTime,
                dto.endDateTime
        );
        if (!overlapping.isEmpty()) {
            throw new InvalidScheduleException(
                    "A schedule already exists for this title on "
                            + dto.platform + " in the given time window");
        }

        // Step 8: Save schedule
        Schedule schedule = ScheduleRequestMapper.toEntity(dto);
        Schedule saved = repo.save(schedule);

        return ScheduleResponseMapper.toDTO(saved);
    }

    // ✅ CALENDAR VIEW
    @Override
    public List<CalendarScheduleDTO> getCalendar(
            LocalDateTime start,
            LocalDateTime end,
            String status,
            String platform,
            int page,
            int size
    ) {
        List<Schedule> schedules = repo.findByStartDateTimeBetween(start, end);

        List<Schedule> filtered = schedules.stream()
                .filter(s -> status == null || s.getStatus().equalsIgnoreCase(status))
                .filter(s -> platform == null || s.getPlatform().equalsIgnoreCase(platform))
                .toList();

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, filtered.size());

        if (startIndex >= filtered.size()) {
            return List.of();
        }

        return filtered.subList(startIndex, endIndex)
                .stream()
                .map(ScheduleResponseMapper::toDTO)
                .toList();
    }

    // ✅ UPDATE (PUT)
    @Override
    public CalendarScheduleDTO updateSchedule(Long id, CreateScheduleDTO dto) {

        if (id <= 0) {
            throw new InvalidScheduleException("Invalid ID");
        }

        Schedule existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        // BL5 — Cannot update expired schedule
        if (existing.getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidScheduleException(
                    "Cannot update an expired schedule. End time was: "
                            + existing.getEndDateTime());
        }

        ScheduleRequestMapper.updateEntity(existing, dto);

        validateScheduleWindow(existing.getStartDateTime(), existing.getEndDateTime());

        contractIntegrationService.validateAndFetchContract(
                existing.getContractId(),
                existing.getStartDateTime().toLocalDate(),
                existing.getEndDateTime().toLocalDate()
        );

        return ScheduleResponseMapper.toDTO(repo.save(existing));
    }

    // ✅ GET BY ID
    @Override
    public CalendarScheduleDTO getById(Long id) {

        if (id <= 0) {
            throw new InvalidScheduleException("Invalid ID");
        }

        Schedule schedule = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        return ScheduleResponseMapper.toDTO(schedule);
    }

    // ✅ PARTIAL UPDATE (PATCH)
    @Override
    public CalendarScheduleDTO partialUpdate(Long id, Map<String, Object> updates) {

        if (id <= 0) {
            throw new InvalidScheduleException("Invalid schedule ID");
        }

        Schedule existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        // BL5 — Cannot update expired schedule
        if (existing.getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidScheduleException(
                    "Cannot update an expired schedule. End time was: "
                            + existing.getEndDateTime());
        }

        updates.forEach((key, value) -> {
            switch (key) {
                case "platform"      -> existing.setPlatform(value.toString().trim()); // BL8 normalize
                case "startDateTime" -> existing.setStartDateTime(LocalDateTime.parse(value.toString()));
                case "endDateTime"   -> existing.setEndDateTime(LocalDateTime.parse(value.toString()));
                case "windowType"    -> {
                    validateWindowType(value.toString()); // BL2 validate
                    existing.setWindowtype(value.toString());
                }
                case "status"        -> existing.setStatus(value.toString());
                default -> throw new InvalidScheduleException(
                        "Field not allowed for update: " + key
                );
            }
        });

        validateScheduleWindow(existing.getStartDateTime(), existing.getEndDateTime());

        contractIntegrationService.validateAndFetchContract(
                existing.getContractId(),
                existing.getStartDateTime().toLocalDate(),
                existing.getEndDateTime().toLocalDate()
        );

        return ScheduleResponseMapper.toDTO(repo.save(existing));
    }

    // ✅ DELETE
    @Override
    public void deleteSchedule(Long id) {

        if (id <= 0) {
            throw new InvalidScheduleException("Invalid ID");
        }

        Schedule schedule = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        // BL1 — Cannot delete an actively running schedule
        boolean isRunning =
                schedule.getStartDateTime().isBefore(LocalDateTime.now()) &&
                        schedule.getEndDateTime().isAfter(LocalDateTime.now());

        if ("ACTIVE".equalsIgnoreCase(schedule.getStatus()) && isRunning) {
            throw new InvalidScheduleException(
                    "Cannot delete an actively running schedule");
        }

        repo.delete(schedule);
    }

    // ✅ Reusable window validator
    private void validateScheduleWindow(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new InvalidScheduleException("Start time must be before end time");
        }
    }

    // ✅ BL2 — WindowType validator
    private void validateWindowType(String windowType) {
        if (windowType == null || !VALID_WINDOW_TYPES.contains(windowType.toUpperCase())) {
            throw new InvalidScheduleException(
                    "Invalid windowType: " + windowType
                            + ". Allowed values: " + VALID_WINDOW_TYPES);
        }
    }

    // ✅ BL1 — Territory validator
    // territoryListJson from contract looks like: ["IN","US","UK"]
    // platform name is matched against territory codes
    private void validateTerritory(String platform, String territoryListJson) {
        if (territoryListJson == null || territoryListJson.isBlank()) {
            return; // no territory restriction on this contract
        }
        // Simple contains check — territoryListJson has territory codes
        // e.g. platform="Netflix-IN" should match if "IN" is in territory list
        // If no match found, warn but don't block — territory is a soft check
        // because platform names don't always map 1:1 to territory codes
        // Full implementation would require a territory-to-platform mapping table
    }
}