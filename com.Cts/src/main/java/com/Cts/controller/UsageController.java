package com.Cts.controller;

import com.Cts.service.UsageServiceImplementation;
import com.Cts.dto.request.UsageBreakdownDTO;
import com.Cts.dto.request.UsageDetailsDTO;
import com.Cts.dto.request.UsageSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/usage")
public class UsageController {

    @Autowired
    private UsageServiceImplementation service;

    @GetMapping("/summary")
    public UsageSummaryDTO getSummary(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        return service.getSummary(start,end);
    }

    @GetMapping("/breakdown")
    public List<UsageBreakdownDTO> getBreakdown(@RequestParam LocalDate start, @RequestParam LocalDate end, @RequestParam String groupBy) {
        return service.getBreakdown(start,end,groupBy);
    }

    @GetMapping("/details")
    public List<UsageDetailsDTO> getDetails(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        return service.getDetails(start,end);
    }
}
