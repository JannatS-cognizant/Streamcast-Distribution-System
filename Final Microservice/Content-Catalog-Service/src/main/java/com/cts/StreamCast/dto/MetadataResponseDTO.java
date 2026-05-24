package com.cts.StreamCast.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetadataResponseDTO {

    private int id;
    @NotBlank(message = "Key is required")
    private String key;
    @NotBlank(message = "Value is required")
    private String value;

   private TitleDTO titleDTO;
}
