package com.cts.StreamCast.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetDTO {
    private Integer id;
    @NotBlank(message = "Asset type is required")
    private String assetType;
    @NotBlank(message = "File URI is required")
    private String fileURI;
    @NotBlank(message = "Checksum is required")
    private String checksum;
    @NotNull(message = "Duration is required")
    private int duration;
    @NotBlank(message = "Status is required")
    private String status;

    private Integer titleId;
    private String titleName;
}
