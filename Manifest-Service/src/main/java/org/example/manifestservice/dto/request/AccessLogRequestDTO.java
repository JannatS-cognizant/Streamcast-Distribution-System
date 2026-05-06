package org.example.manifestservice.dto.request;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AccessLogRequestDTO {
    private String accessedAt;
    private String action;
}
