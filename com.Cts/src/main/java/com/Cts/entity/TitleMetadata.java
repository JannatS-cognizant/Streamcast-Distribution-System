package com.Cts.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data

@NoArgsConstructor
@AllArgsConstructor

@Table(name = "title_metadata")

public class TitleMetadata {

	@Id

	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)

	private Integer id;

	@NotNull(message = "Title ID is required")

	private int titleId;

	@NotBlank(message = "Key is required")
	@Column(name = "meta_key")
	private String key;

	@NotBlank(message = "Value is required")

	private String value;

	private LocalDateTime updatedAt;

}
