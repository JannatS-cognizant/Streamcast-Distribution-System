package com.cts.schedule_service.Feign;

import lombok.Data;

@Data
public class UsageBreakdownDTO {
    public String group;
    public Long views;
    public Double revenue;
}
