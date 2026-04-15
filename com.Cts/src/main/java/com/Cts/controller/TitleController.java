package com.Cts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Cts.entity.Title;
import com.Cts.ExceptionHandler.TitlenotFoundException;
import com.Cts.service.TitleService;

import jakarta.validation.Valid;

@RestController

@RequestMapping("/api/titles")

@CrossOrigin

public class TitleController {

	@Autowired

	private TitleService service;

	@PostMapping
	//Customize HTTP response
	public ResponseEntity<?> createTitle(@Valid @RequestBody Title title) {

		Title created = service.createTitle(title);

		return ResponseEntity.status(HttpStatus.CREATED).body(created);

	}

	@GetMapping

	public ResponseEntity<List<Title>> getAllTitles() {

		return ResponseEntity.ok(service.getAllTitles());

	}

	@GetMapping("/{id}")

	public ResponseEntity<?> getTitleById(@PathVariable int id) {

		try {

			return ResponseEntity.ok(service.getTitleById(id));

		} catch (TitlenotFoundException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

	@PutMapping("/{id}")

	public ResponseEntity<?> updateTitle(@PathVariable int id,@Valid @RequestBody Title title) {

		try {

			return ResponseEntity.ok(service.updateTitle(id, title));

		} catch (TitlenotFoundException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

	@DeleteMapping("/{id}")

	public ResponseEntity<?> deleteTitle(@PathVariable int id) {

		try {

			service.deleteTitle(id);

			return ResponseEntity.ok("Title deleted successfully");

		} catch (TitlenotFoundException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

}