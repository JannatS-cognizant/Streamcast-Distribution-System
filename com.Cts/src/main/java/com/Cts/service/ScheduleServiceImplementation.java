package com.Cts.service;
import com.Cts.entity.Schedule;
import com.Cts.ExceptionHandler.InvalidScheduleException;
import com.Cts.ExceptionHandler.ResourceNotFoundException;
import com.Cts.repository.ConflictRepository;
import com.Cts.repository.ScheduleRepository;
import com.Cts.dto.request.CreateScheduleDTO;
import com.Cts.dto.request.CalendarScheduleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleServiceImplementation implements ScheduleService  {

    @Autowired
    private ScheduleRepository repo;

    @Autowired
    private ConflictRepository ConflictRepo;

    //for Schedule calendar view screen api with all filters like time, platform in the form of pagination
    @Override
    public List<CalendarScheduleDTO> getCalendar(LocalDateTime start, LocalDateTime end, String status, String platform, int page, int size) {


        //filtering with respect to start and end date,time
        List<Schedule> schedules = repo.findByStartDateTimeBetween(start, end);

        //filtering with respect to status and platform
        List<Schedule> filtered = schedules.stream()
                .filter(s-> status == null || s.getStatus().equalsIgnoreCase(status))
                .filter(s->platform == null || s.getPlatform().equalsIgnoreCase(platform))
                .toList();


        //pagination with respect to indexing of start and end
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size , filtered.size());

        // checking the condition whether the index is more than the entire size of filter so that it can print clearly
        if(startIndex > filtered.size()){
            return List.of();
        }

        //storing the values in paginated variable
        List<Schedule> paginated = filtered.subList(startIndex, endIndex);

        //returning the paginated values
        return paginated.stream().map(this::mapToDTO).toList();
    }




    // for creating schedule with all the entities
    @Override
    public CalendarScheduleDTO createSchedule(CreateScheduleDTO dto){

        validate(dto);

        Schedule s = new Schedule();

        s.setTitleId(dto.titleId);
        s.setContractId(dto.contractId);
        s.setPlatform(dto.platform);
        s.setStartDateTime(dto.startDateTime);
        s.setEndDateTime(dto.endDateTime);
        s.setWindowtype(dto.windowType);
        s.setStatus("ACTIVE");

        Schedule saved = repo.save(s);



        return mapToDTO(saved);
    }

    @Override
    public CalendarScheduleDTO updateSchedule(Long id, CreateScheduleDTO dto){

        if(id <= 0){
            throw new InvalidScheduleException("Invalid ID");
        }

        Schedule existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        existing.setPlatform(dto.platform);
        existing.setStartDateTime(dto.startDateTime);
        existing.setEndDateTime(dto.endDateTime);
        existing.setWindowtype(dto.windowType);

        Schedule updated = repo.save(existing);

        return mapToDTO(updated);
    }



    //Common for Creating and Updating
    //for creating schedule detecting conflict by checking the time overlapping

    //for Creating Schedule screen fetching the schedule by id
    @Override
    public CalendarScheduleDTO getById(Long id){

        if(id <= 0){
            throw new InvalidScheduleException("Invalid ID");
        }
        Schedule s = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        return mapToDTO(s);
    }

    @Override
    public void deleteSchedule(Long id){

        if(id <= 0){
            throw new InvalidScheduleException("Invalid ID");
        }
        Schedule s = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        repo.delete(s);
    }




    // like printing statement used to return the values which is operated, accessed
    private CalendarScheduleDTO mapToDTO(Schedule s){
        CalendarScheduleDTO dto = new CalendarScheduleDTO();
        dto.scheduleId = s.getScheduleId();
        dto.platform = s.getPlatform();
        dto.startDateTime = s.getStartDateTime();
        dto.endDateTime = s.getEndDateTime();
        dto.status = s.getStatus();
        return dto;
    }

    private void validate(CreateScheduleDTO dto){
        if(dto.startDateTime.isAfter(dto.endDateTime)){
            throw new InvalidScheduleException("Start time must be before end time");
        }
    }

}

