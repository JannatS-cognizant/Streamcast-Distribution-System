package com.cts.StreamCast.Entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "titles")
public class Title {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private int id;
	@NotBlank(message = "Name should not be empty")

	private String name;

	@NotNull(message = "Release date is required")

	private LocalDate releaseDate;

	@NotBlank(message = "Genre should not be empty")

	private String genre;

	@NotBlank(message = "Language should not be empty")

	private String language;

	@NotBlank(message = "Status should not be empty")

	private String status;

	public Title(String name, LocalDate releaseDate, String genre, String language, String status) {
		super();
		this.name = name;
		this.releaseDate = releaseDate;
		this.genre = genre;
		this.language = language;
		this.status = status;
	}
	@OneToMany(mappedBy = "title",cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Asset> assets;
	@OneToMany(mappedBy = "title", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<TitleMetadata> metadataList;
}
