package com.cts.schedule_service.Feign.usage;

import java.time.LocalDate;

public class UsageDetailsDTO {
    public Long titleId;
    public String platform;
    public LocalDate date;
    public Long views;
    public Double revenue;
}
