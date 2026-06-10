package com.cts.StreamCast.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetDTORequest {
    @JsonIgnore
    private Integer id;
    @NotBlank(message = "Asset type is required")
    private String assetType;
    @NotBlank(message = "File URI is required")
    private String fileURI;
    @NotBlank(message = "Checksum is required")
    private String checksum;
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be greater than 0")
    private int duration;
    @NotBlank(message = "Status is required")
    private String status;
    @JsonIgnore
    private Integer titleId;
}
