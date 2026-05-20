package com.cts.schedule_service.Feign;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UsageDetailsDTO {
    public Long titleId;
    public String platform;
    public LocalDate date;
    public Long views;
    public Double revenue;
}
