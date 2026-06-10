package com.cts.StreamCast.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetadataRequestDTO {
    @NotBlank(message = "Key is required")
    private String key;
    @NotBlank(message = "Value is required")
    private String value;
    @JsonIgnore
    private int id;
    @JsonIgnore
    private String name;
}
