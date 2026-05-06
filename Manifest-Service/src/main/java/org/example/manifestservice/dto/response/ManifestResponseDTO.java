package org.example.manifestservice.dto.response;

import lombok.Data;

@Data
public class ManifestResponseDTO {

    private Long manifestId;
    private String titleId;
    private String assetIdsJSON;
    private String destination;
    private String createdBy;
    private String createdAt;
    private String status;
    private Long partnerId;
}
