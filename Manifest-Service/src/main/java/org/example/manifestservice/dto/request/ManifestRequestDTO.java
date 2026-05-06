package org.example.manifestservice.dto.request;
import lombok.Data;

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
