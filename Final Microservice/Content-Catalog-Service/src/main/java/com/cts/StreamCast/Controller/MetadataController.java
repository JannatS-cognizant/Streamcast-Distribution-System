package com.cts.StreamCast.Controller;

import com.cts.StreamCast.dto.MetadataRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.StreamCast.Entity.TitleMetadata;
import com.cts.StreamCast.Exception.MetadataNotFoundException;
import com.cts.StreamCast.Service.MetadataService;

import jakarta.validation.Valid;

@RestController

@RequestMapping("/api")

@CrossOrigin

public class MetadataController {

	@Autowired

	private MetadataService service;

//  GET Metadata by Title

	@PreAuthorize("hasAnyRole('ADMIN','CONTENT_OWNER','RIGHTS_MANAGER')")
	@GetMapping("/titles/{titleId}/metadata")

	public ResponseEntity<?> getMetadata(@PathVariable int titleId) {
		return ResponseEntity.ok(service.getMetadataByTitleId(titleId));
	}

// CREATE Metadata

	@PreAuthorize("hasAnyRole('ADMIN','CONTENT_OWNER')")
	@PostMapping("/titles/{titleId}/metadata")

	public ResponseEntity<?> createMetadata(@PathVariable int titleId,@Valid @RequestBody MetadataRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.createMetadata(titleId, dto));

	}

// UPDATE Metadata

	@PreAuthorize("hasAnyRole('ADMIN','CONTENT_OWNER')")
	@PutMapping("/metadata/{id}")

	public ResponseEntity<?> updateMetadata(@PathVariable int id,@Valid @RequestBody MetadataRequestDTO dto) {

		try {
			return ResponseEntity.ok(service.updateMetadata(id, dto));
		} catch (MetadataNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}



//  DELETE Metadata

	@PreAuthorize("hasRole('ADMIN')")
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
