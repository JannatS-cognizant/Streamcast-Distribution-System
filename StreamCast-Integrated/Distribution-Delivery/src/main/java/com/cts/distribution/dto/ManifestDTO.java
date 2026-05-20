package com.cts.distribution.dto;
 
import lombok.Data;
 
@Data
public class ManifestDTO {
    private String titleId;
    private String assetIdsJSON;
    private String destination;
    private String createdBy;
    private String status;
    private Long partnerId;
}