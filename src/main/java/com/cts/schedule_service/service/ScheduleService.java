package com.cts.schedule_service.service;



import com.cts.schedule_service.dto.CalendarScheduleDTO;
import com.cts.schedule_service.dto.CreateScheduleDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface ScheduleService  {

    // for Schedule Calendar API
    List<CalendarScheduleDTO> getCalendar(LocalDateTime start, LocalDateTime end, String status, String platform, int page, int size);

    //for Creating schedule API
    CalendarScheduleDTO createSchedule(CreateScheduleDTO dto); //creating scheduling
    CalendarScheduleDTO getById(Long id); //
    CalendarScheduleDTO updateSchedule(Long id, CreateScheduleDTO dto);


    CalendarScheduleDTO partialUpdate(Long id, Map<String, Object> updates);


    //for Schedule Details Screen
    void deleteSchedule(Long id);







}
