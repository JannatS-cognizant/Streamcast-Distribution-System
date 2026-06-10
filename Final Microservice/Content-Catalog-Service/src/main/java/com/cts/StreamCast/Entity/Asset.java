package com.cts.StreamCast.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
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
	private String assetType; // VIDEO, AUDIO, SUBTITLE, IMAGE, DOCUMENT
	private String fileURI;
	private String checksum;
	private Integer duration; // in seconds
	private String status;

	@ManyToOne
	@JoinColumn(name = "title_id")
	private Title title;
}
