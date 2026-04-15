package com.Cts.controller;

import com.Cts.entity.Conflict;
import com.Cts.repository.ConflictRepository;
import com.Cts.service.ConflictService;
import com.Cts.dto.request.ConflictDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/conflicts")
public class ConflictController {


    @Autowired
    private ConflictService service;

    //get the conflict data by schedule Id
    @GetMapping
    public List<ConflictDTO> getBySchedule(@RequestParam  Long schedulesId){
        return service.getConflictBySchedule(schedulesId);

    }

    //get the conflict data by conflict id
    @GetMapping("/{id}")
    public ConflictDTO getConflictById(@PathVariable  Long id)
    {
        return service.getConflictById(id);

    }

    // using put method to resolve the conflict by the generated conflict id
    @PutMapping("/{id}/resolve")
    public ConflictDTO resolve(@PathVariable  Long id){
        return service.resolveConflict(id);
    }


}
