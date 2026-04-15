package com.Cts.service;

import com.Cts.dto.request.CreateScheduleDTO;
import com.Cts.dto.request.CalendarScheduleDTO;

import java.time.LocalDateTime;
import java.util.List;


public interface ScheduleService  {

    // for Schedule Calendar API
    List<CalendarScheduleDTO> getCalendar(LocalDateTime start, LocalDateTime end, String status, String platform, int page, int size);

    //for Creating schedule API
    CalendarScheduleDTO createSchedule(CreateScheduleDTO dto); //creating scheduling
    CalendarScheduleDTO getById(Long id); //
    CalendarScheduleDTO updateSchedule(Long id, CreateScheduleDTO dto);

    //for Schedule Details Screen
    void deleteSchedule(Long id);






}
