package com.Cts.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "assets")

public class Asset {
	@Id

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Integer id;

	@NotNull(message = "Title ID is required")

	private int titleId;

	@NotBlank(message = "Asset type is required")

	private String assetType; // VIDEO, AUDIO, SUBTITLE, IMAGE, DOCUMENT

	@NotBlank(message = "File URI is required")

	private String fileURI;

	@NotBlank(message = "Checksum is required")

	private String checksum;

	@NotNull(message = "Duration is required")

	private Integer duration; // in seconds

	@NotBlank(message = "Status is required")

	private String status;
}
