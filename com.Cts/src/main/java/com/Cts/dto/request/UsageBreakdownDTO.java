package com.Cts.dto.request;

import lombok.Data;

@Data
public class UsageBreakdownDTO {
    public String group;
    public Long views;
    public Double revenue;
}
