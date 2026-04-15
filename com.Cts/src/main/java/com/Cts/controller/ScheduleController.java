package com.Cts.controller;

import com.Cts.service.ConflictService;
import com.Cts.service.ScheduleService;
import com.Cts.dto.request.CreateScheduleDTO;
import com.Cts.dto.request.CalendarScheduleDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    @Autowired
    private ScheduleService service;
    @Autowired
    private ConflictService conflictService;
    // using get method to fetch and display the calendar view of the schedules by filtering it by entities and viewing in page with size
    @GetMapping("/calendar")
    public List<CalendarScheduleDTO> getCalendar(
            @RequestParam  LocalDateTime start,
            @RequestParam  LocalDateTime end,
            @RequestParam (required = false) String status,
            @RequestParam (required = false) String platform,
            @RequestParam (defaultValue = "0")  int page,
            @RequestParam (defaultValue = "5")  int size
    ){
        return service.getCalendar(start,end,status, platform, page, size);
    }
    //creating
    @PostMapping
    public CalendarScheduleDTO create(@Valid @RequestBody CreateScheduleDTO dto){
        return service.createSchedule(dto);
    }
    //fetch by id
    @GetMapping("/{id}")
    public CalendarScheduleDTO getById(@PathVariable Long id){
        return service.getById(id);
    }
    //update by id
    @PutMapping("/{id}")
    public CalendarScheduleDTO update(@PathVariable Long id, @RequestBody CreateScheduleDTO dto){
        return service.updateSchedule(id,dto);
    }
    //delete the scheudle by id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        service.deleteSchedule(id);
        return ResponseEntity.ok("Schedule deleted successfully");
    }
    //detect conflicts
    @PostMapping("/{id}/detect-conflicts")
    public ResponseEntity<?> detect(@PathVariable Long id) {
        conflictService.detectConflicts(id);
        return ResponseEntity.ok("Conflict detection completed");
    }






}
