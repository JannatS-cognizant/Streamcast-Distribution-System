package com.Cts.dto.request;

import lombok.Data;

@Data
public class UsageSummaryDTO {
    public Long totalViews;
    public Double totalRevenue;
    public Long totalRecords;
}
