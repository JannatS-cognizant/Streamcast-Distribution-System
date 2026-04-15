package com.Cts.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccessLogResponseDTO {
    private Long logId;
    private String accessedAt;
    private String action;
    private Long partnerId;
}
