package com.Cts.service;

import com.Cts.repository.UsageRepository;
import com.Cts.dto.request.UsageBreakdownDTO;
import com.Cts.dto.request.UsageDetailsDTO;
import com.Cts.dto.request.UsageSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsageServiceImplementation{

    @Autowired
    private UsageRepository repo;

    public UsageSummaryDTO getSummary(LocalDate start, LocalDate end) {
        Object[] data = (Object[]) repo.getSummary(start,end);

        UsageSummaryDTO dto = new UsageSummaryDTO();

        dto.totalViews = data[0] != null ? (Long)  data[0] : 0;
        dto.totalRevenue = data[1] != null ? (Double)  data[1] : 0.0;
        dto.totalRecords = data[2] != null ? (Long) data[2] : 0;
        return dto;
    }

    public List<UsageBreakdownDTO> getBreakdown(LocalDate start, LocalDate end, String groupBy) {
        List<Object[]> results;
        if("platform".equalsIgnoreCase(groupBy)) {
            results = repo.getPlatformBreakdown(start,end);
        }else{
            results = repo.getTitleBreakdown(start,end);
        }

        return results.stream().map(r->{
            UsageBreakdownDTO dto = new UsageBreakdownDTO();
            dto.group = String.valueOf(r[0]);
            dto.views = (Long)  r[1];
            dto.revenue = (Double)  r[2];
            return dto;
        }).toList();


    }

    public List<UsageDetailsDTO> getDetails(LocalDate start, LocalDate end) {
        return repo.findByDateBetween(start,end).stream().map(u->{
            UsageDetailsDTO dto = new UsageDetailsDTO();
            dto.titleId = u.getTitleId();
            dto.platform = u.getPlatform();
            dto.date = u.getDate();
            dto.views = u.getViews();
            dto.revenue = u.getRevenue();
            return dto;
        }).toList();

    }
}
