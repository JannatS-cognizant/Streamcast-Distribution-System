package com.cts.schedule_service.service;

import com.cts.schedule_service.Feign.Contract.ContractResponseDTO;
import com.cts.schedule_service.dto.ConflictDTO;
import com.cts.schedule_service.entity.Conflict;
import com.cts.schedule_service.entity.Schedule;
import com.cts.schedule_service.exception.InvalidScheduleException;
import com.cts.schedule_service.exception.ResourceNotFoundException;
import com.cts.schedule_service.mapper.request.ConflictRequestMapper;
import com.cts.schedule_service.mapper.response.ConflictResponseMapper;
import com.cts.schedule_service.repository.ConflictRepository;
import com.cts.schedule_service.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConflictServiceImplementation implements ConflictService {

    private final ScheduleRepository scheduleRepo;
    private final ConflictRepository conflictRepo;
    private final ContractIntegrationService contractService;   // ✅ for exclusivity check

    public ConflictServiceImplementation(
            ScheduleRepository scheduleRepo,
            ConflictRepository conflictRepo,
            ContractIntegrationService contractService
    ) {
        this.scheduleRepo = scheduleRepo;
        this.conflictRepo = conflictRepo;
        this.contractService = contractService;
    }

    // ✅ Detect conflicts manually — user triggered
    @Override
    public void detectConflicts(Long scheduleId) {

        if (scheduleId <= 0) {
            throw new InvalidScheduleException("Invalid schedule ID");
        }

        Schedule current = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        List<Schedule> schedules = scheduleRepo.findAll();

        // ── Part 1: Standard overlap check ──────────────────────────────
        for (Schedule other : schedules) {

            if (!other.getScheduleId().equals(current.getScheduleId())) {

                boolean overlap =
                        current.getStartDateTime().isBefore(other.getEndDateTime()) &&
                                current.getEndDateTime().isAfter(other.getStartDateTime());

                boolean samePlatform =
                        current.getPlatform().equalsIgnoreCase(other.getPlatform());

                // ✅ Bug fix: same title check — different titles on same platform is NOT a conflict
                boolean sameTitleId =
                        current.getTitleId().equals(other.getTitleId());

                // ✅ Bug fix: duplicate guard — don't save if already recorded
                boolean alreadyExists =
                        conflictRepo.existsByScheduleId1AndScheduleId2(
                                current.getScheduleId(), other.getScheduleId()) ||
                                conflictRepo.existsByScheduleId1AndScheduleId2(
                                        other.getScheduleId(), current.getScheduleId());

                if (overlap && samePlatform && sameTitleId && !alreadyExists) {
                    conflictRepo.save(
                            ConflictRequestMapper.toEntity(
                                    current.getScheduleId(),
                                    other.getScheduleId()
                            )
                    );
                }
            }
        }

        // ── Part 2: Business Logic 6 — Exclusivity check ────────────────
        // If contract has exclusivityFlag=true, flag conflict with ANY overlapping
        // schedule for same title — regardless of platform
        try {
            ContractResponseDTO contract = contractService.validateAndFetchContract(
                    current.getContractId(),
                    current.getStartDateTime().toLocalDate(),
                    current.getEndDateTime().toLocalDate()
            );

            if (Boolean.TRUE.equals(contract.getExclusivityFlag())) {

                List<Schedule> exclusivityConflicts = scheduleRepo.findByTitleIdAndOverlapping(
                        current.getTitleId(),
                        current.getScheduleId(),
                        current.getStartDateTime(),
                        current.getEndDateTime()
                );

                for (Schedule exc : exclusivityConflicts) {

                    boolean alreadyExists =
                            conflictRepo.existsByScheduleId1AndScheduleId2(
                                    current.getScheduleId(), exc.getScheduleId()) ||
                                    conflictRepo.existsByScheduleId1AndScheduleId2(
                                            exc.getScheduleId(), current.getScheduleId());

                    if (!alreadyExists) {
                        conflictRepo.save(
                                ConflictRequestMapper.toEntity(
                                        current.getScheduleId(),
                                        exc.getScheduleId()
                                )
                        );
                    }
                }
            }

        } catch (InvalidScheduleException e) {
            // Contract-Service unavailable — skip exclusivity check gracefully
            // Standard overlap conflicts are still saved above
        }
    }

    // ✅ Get conflicts for a schedule
    @Override
    public List<ConflictDTO> getConflictBySchedule(Long scheduleId) {

        if (scheduleId <= 0) {
            throw new InvalidScheduleException("Invalid schedule ID");
        }

        return conflictRepo
                .findByScheduleId1OrScheduleId2(scheduleId, scheduleId)
                .stream()
                .map(ConflictResponseMapper::toDTO)
                .toList();
    }

    // ✅ Get conflict by ID
    @Override
    public ConflictDTO getConflictById(Long id) {

        if (id <= 0) {
            throw new InvalidScheduleException("Invalid conflict ID");
        }

        Conflict conflict = conflictRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conflict not found"));

        return ConflictResponseMapper.toDTO(conflict);
    }

    // ✅ Resolve conflict (manual, once)
    @Override
    public ConflictDTO resolveConflict(Long id) {

        if (id <= 0) {
            throw new InvalidScheduleException("Invalid conflict ID");
        }

        Conflict conflict = conflictRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conflict not found"));

        if (conflict.isResolved()) {
            throw new InvalidScheduleException("Conflict is already resolved");
        }

        conflict.setResolved(true);
        return ConflictResponseMapper.toDTO(conflictRepo.save(conflict));
    }
}