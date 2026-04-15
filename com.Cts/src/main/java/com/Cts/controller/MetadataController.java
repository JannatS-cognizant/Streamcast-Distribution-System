package com.Cts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Cts.entity.TitleMetadata;
import com.Cts.ExceptionHandler.MetadataNotFoundException;
import com.Cts.service.MetadataService;

import jakarta.validation.Valid;

@RestController

@RequestMapping("/api")

@CrossOrigin

public class MetadataController {

	@Autowired

	private MetadataService service;

//  GET Metadata by Title

	@GetMapping("/titles/{id}/metadata")

	public ResponseEntity<?> getMetadataByTitle(@PathVariable int id) {

		try {

			return ResponseEntity.ok(service.getMetadataByTitleId(id));

		} catch (RuntimeException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

// CREATE Metadata

	@PostMapping("/metadata")

	public ResponseEntity<?> createMetadata(@Valid @RequestBody TitleMetadata metadata) {

		try {

			return ResponseEntity.status(HttpStatus.CREATED)

					.body(service.createMetadata(metadata));

		} catch (RuntimeException e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

		}

	}

// UPDATE Metadata

	@PutMapping("/metadata/{id}")

	public ResponseEntity<?> updateMetadata(@PathVariable int id, @Valid @RequestBody TitleMetadata metadata) {

		try {

			return ResponseEntity.ok(service.updateMetadata(id, metadata));

		} catch (MetadataNotFoundException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

//  DELETE Metadata

	@DeleteMapping("/metadata/{id}")

	public ResponseEntity<?> deleteMetadata(@PathVariable int id) {

		try {

			service.deleteMetadata(id);

			return ResponseEntity.ok("Metadata deleted successfully");

		} catch (MetadataNotFoundException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

}
