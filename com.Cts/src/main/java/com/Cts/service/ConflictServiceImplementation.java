package com.Cts.service;

import com.Cts.entity.Conflict;
import com.Cts.entity.Schedule;
import com.Cts.ExceptionHandler.InvalidScheduleException;
import com.Cts.repository.ConflictRepository;
import com.Cts.repository.ScheduleRepository;
import com.Cts.dto.request.ConflictDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConflictServiceImplementation implements ConflictService {

    @Autowired
    private ScheduleRepository scheduleRepo;

    @Autowired
    private ConflictRepository conflictRepo;

    //used to detect the conflict based on the scheduleId
    @Override
    public void detectConflicts(Long scheduleId) {

        if(scheduleId <= 0){
            throw new InvalidScheduleException("Invalid Schedule ID");
        }

        Schedule current = scheduleRepo.findById(scheduleId).orElseThrow(() -> new RuntimeException("Schedule not found"));

        List<Schedule> all = scheduleRepo.findAll();



        // the overlap concept is the input start date should start before the current end date and the input end date should end after the current start date
        for (Schedule s : all) {
            if (!s.getScheduleId().equals(scheduleId)) {
                boolean overlap = current.getStartDateTime().isBefore(s.getEndDateTime()) &&
                        current.getEndDateTime().isAfter(s.getStartDateTime());

                if (overlap) {

                    Conflict c = new Conflict();
                    c.setScheduleId(scheduleId);
                    c.setConflictType("TIME_OVERLAP");
                    c.setResolved(false);

                    //the details is saved in the database
                    conflictRepo.save(c);

                    //then the new schedule status is updated as conflict
                    current.setStatus("CONFLICT");
                    scheduleRepo.save(current);
                }


            }
        }


    }

    @Override
    public List<ConflictDTO> getConflictBySchedule(Long scheduleId){

        if(scheduleId <= 0){
            throw new InvalidScheduleException("Invalid Schedule ID");
        }

        return conflictRepo.findByScheduleId(scheduleId).stream().map(this::mapToDTO).toList();
    }

    @Override
    public ConflictDTO getConflictById(Long id){

        if(id <= 0){
            throw new InvalidScheduleException("Invalid ID");
        }

        Conflict c = conflictRepo.findById(id).orElseThrow(() -> new RuntimeException("Conflict not found"));
        return mapToDTO(c);
    }

    @Override
    public  ConflictDTO resolveConflict(Long id){

        if(id <= 0){
            throw new InvalidScheduleException("Invalid ID");
        }

        Conflict c = conflictRepo.findById(id).orElseThrow(() -> new RuntimeException("Conflict not found"));
        c.setResolved(true);

        Conflict updated = conflictRepo.save(c);

        return mapToDTO(updated);
    }

    private ConflictDTO mapToDTO(Conflict c){
        ConflictDTO dto = new ConflictDTO();
        dto.conflictId = c.getConflictId();
        dto.scheduleId = c.getScheduleId();
        dto.conflictType = c.getConflictType();
        dto.resolved = c.getResolved();
        return dto;
    }
}
