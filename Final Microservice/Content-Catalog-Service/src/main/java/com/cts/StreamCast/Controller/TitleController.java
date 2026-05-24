package com.cts.StreamCast.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cts.StreamCast.Entity.Title;
import com.cts.StreamCast.Exception.TitlenotFoundException;
import com.cts.StreamCast.Service.TitleService;

import jakarta.validation.Valid;

@RestController

@RequestMapping("/api/titles")

@CrossOrigin

public class TitleController {

	@Autowired

	private TitleService service;

	@PreAuthorize("hasAnyRole('ADMIN','CONTENT_OWNER')")
	@PostMapping
	//Customize HTTP response
	public ResponseEntity<?> createTitle(@Valid @RequestBody Title title) {

		Title created = service.createTitle(title);

		return ResponseEntity.status(HttpStatus.CREATED).body(created);

	}

	@PreAuthorize("hasAnyRole('ADMIN','CONTENT_OWNER','RIGHTS_MANAGER','SCHEDULER')")
	@GetMapping
	public ResponseEntity<List<Title>> getAllTitles() {

		return ResponseEntity.ok(service.getAllTitles());

	}

	@PreAuthorize("hasAnyRole('ADMIN','CONTENT_OWNER','RIGHTS_MANAGER','SCHEDULER')")
	@GetMapping("/{id}")
	public ResponseEntity<?> getTitleById(@PathVariable int id) {

		try {

			return ResponseEntity.ok(service.getTitleById(id));

		} catch (TitlenotFoundException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

	@PreAuthorize("hasAnyRole('ADMIN','CONTENT_OWNER')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateTitle(@PathVariable int id,@Valid @RequestBody Title title) {

		try {

			return ResponseEntity.ok(service.updateTitle(id, title));

		} catch (TitlenotFoundException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteTitle(@PathVariable int id) {

		try {

			service.deleteTitle(id);

			return ResponseEntity.ok("Title deleted successfully");

		} catch (TitlenotFoundException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}
	
	@PreAuthorize("hasAnyRole('ADMIN','CONTENT_OWNER','SCHEDULER','RIGHTS_MANAGER')")
	@GetMapping("/{id}/exists")
	public ResponseEntity<Boolean> titleExists(@PathVariable int id) {
	    try {
	       service.getTitleById(id);
	       return ResponseEntity.ok(true);   // title found → true
	    } catch (TitlenotFoundException e) {
	       return ResponseEntity.ok(false);  // title not found → false
	    }
	}

}