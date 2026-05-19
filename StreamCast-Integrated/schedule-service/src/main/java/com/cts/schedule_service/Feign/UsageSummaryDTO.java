package com.cts.schedule_service.Feign;

import lombok.Data;

@Data
public class UsageSummaryDTO {
    public Long totalViews;
    public Double totalRevenue;
    public Long totalRecords;
}
