package com.Cts.dto.request;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ManifestRequestDTO {

    private String titleId;
    private String assetIdsJSON;
    private String destination;
    private String createdBy;
    private String createdAt;
    private String status;
    private Long partnerId;
}
