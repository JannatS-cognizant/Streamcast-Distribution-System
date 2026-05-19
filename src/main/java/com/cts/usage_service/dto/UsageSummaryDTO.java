package com.cts.usage_service.dto;

import lombok.Data;

@Data
public class UsageSummaryDTO {
    public Long totalViews;
    public Double totalRevenue;
    public Long totalRecords;
}
