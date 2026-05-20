package com.cts.usage_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UsageDetailsDTO {
    public Long titleId;
    public String platform;
    public LocalDate date;
    public Long views;
    public Double revenue;
}
