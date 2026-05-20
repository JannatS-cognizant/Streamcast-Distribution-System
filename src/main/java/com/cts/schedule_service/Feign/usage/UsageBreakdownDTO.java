package com.cts.schedule_service.Feign.usage;

public class UsageBreakdownDTO {
    public Object group;   // ✅ allow Long / String / LocalDate
    public Long views;
    public Double revenue;

    // ✅ IMPORTANT: constructor MUST exist
    public UsageBreakdownDTO(Object group, Long views, Double revenue) {
        this.group = group;
        this.views = views;
        this.revenue = revenue;
    }
}
