package com.cts.schedule_service.service;

//import com.cts.schedule_service.Feign.ContractClient;
import com.cts.schedule_service.Feign.TitleClient;
import com.cts.schedule_service.dto.CalendarScheduleDTO;
import com.cts.schedule_service.dto.CreateScheduleDTO;
import com.cts.schedule_service.entity.Schedule;
import com.cts.schedule_service.exception.InvalidScheduleException;
import com.cts.schedule_service.exception.ResourceNotFoundException;
import com.cts.schedule_service.mapper.request.ScheduleRequestMapper;
import com.cts.schedule_service.mapper.response.ScheduleResponseMapper;
import com.cts.schedule_service.repository.ScheduleRepository;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImplementation implements ScheduleService {

    private final ScheduleRepository repo;
    private final TitleClient titleClient;
//    private final ContractClient contractClient;

    public ScheduleServiceImplementation(
            ScheduleRepository repo,
            TitleClient titleClient
//            ContractClient contractClient
    ) {
        this.repo = repo;
        this.titleClient = titleClient;
//        this.contractClient = contractClient;
    }

    // ✅ Retry will attempt 3 times if feign call fails
    @Retry(name = "feignRetry", fallbackMethod = "titleFallback")
    public boolean validateTitle(Integer titleId) {
        return titleClient.isTitleExists(Long.valueOf(titleId));
    }

//    // ✅ Retry will attempt 3 times if feign call fails
//    @Retry(name = "feignRetry", fallbackMethod = "contractFallback")
//    public boolean validateContract(Long contractId, LocalDateTime start, LocalDateTime end) {
//        return contractClient.isContractValid(
//                contractId,
//                start.toString(),
//                end.toString()
//        );
//    }

    // ✅ Fallback for title — called after all 3 retries fail
    public boolean titleFallback(Long titleId, Exception ex) {
        throw new InvalidScheduleException(
                "catalog-service unavailable after retries. Could not validate titleId: " + titleId
        );
    }

    // ✅ Fallback for contract — called after all 3 retries fail
    // For demo: returns true since contract-service is not ready yet
    public boolean contractFallback(Long contractId, LocalDateTime start, LocalDateTime end, Exception ex) {
        return true; // ← remove this line once contract-service team is ready
    }

    @Override
    public CalendarScheduleDTO createSchedule(CreateScheduleDTO dto) {

        if (!validateTitle(dto.titleId)) {
            throw new InvalidScheduleException("Invalid titleId");
        }

//        if (!validateContract(dto.contractId, dto.startDateTime, dto.endDateTime)) {
//            throw new InvalidScheduleException("Invalid contractId");
//        }

        validateScheduleWindow(dto.startDateTime, dto.endDateTime);

        Schedule schedule = ScheduleRequestMapper.toEntity(dto);
        Schedule saved = repo.save(schedule);

        return ScheduleResponseMapper.toDTO(saved);
    }

    // ✅ Calendar view
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

    // ✅ Update schedule (PUT)
    @Override
    public CalendarScheduleDTO updateSchedule(Long id, CreateScheduleDTO dto) {

        if (id <= 0) {
            throw new InvalidScheduleException("Invalid ID");
        }

        Schedule existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        ScheduleRequestMapper.updateEntity(existing, dto);
        validateScheduleWindow(existing.getStartDateTime(), existing.getEndDateTime());

        return ScheduleResponseMapper.toDTO(repo.save(existing));
    }

    // ✅ Get by ID
    @Override
    public CalendarScheduleDTO getById(Long id) {

        if (id <= 0) {
            throw new InvalidScheduleException("Invalid ID");
        }

        Schedule schedule = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        return ScheduleResponseMapper.toDTO(schedule);
    }

    // ✅ Partial update (PATCH)
    @Override
    public CalendarScheduleDTO partialUpdate(Long id, Map<String, Object> updates) {

        if (id <= 0) {
            throw new InvalidScheduleException("Invalid schedule ID");
        }

        Schedule existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "platform" -> existing.setPlatform(value.toString());
                case "startDateTime" -> existing.setStartDateTime(LocalDateTime.parse(value.toString()));
                case "endDateTime" -> existing.setEndDateTime(LocalDateTime.parse(value.toString()));
                case "windowType" -> existing.setWindowtype(value.toString());
                default -> throw new InvalidScheduleException(
                        "Field not allowed for update: " + key
                );
            }
        });

        validateScheduleWindow(existing.getStartDateTime(), existing.getEndDateTime());
        return ScheduleResponseMapper.toDTO(repo.save(existing));
    }

    // ✅ Delete schedule
    @Override
    public void deleteSchedule(Long id) {

        if (id <= 0) {
            throw new InvalidScheduleException("Invalid ID");
        }

        Schedule schedule = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        repo.delete(schedule);
    }

    // ✅ Single validation helper
    private void validateScheduleWindow(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new InvalidScheduleException("Start time must be before end time");
        }
    }
}