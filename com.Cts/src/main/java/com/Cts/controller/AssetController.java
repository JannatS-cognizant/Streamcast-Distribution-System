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

import com.Cts.entity.Asset;
import com.Cts.ExceptionHandler.AssetNotFoundException;
import com.Cts.service.AssetService;

import jakarta.validation.Valid;

@RestController

@RequestMapping("/api")

@CrossOrigin

public class AssetController {

	@Autowired

	private AssetService service;

// GET Assets by Title ID

	@GetMapping("/titles/{id}/assets")

	public ResponseEntity<?> getAssetsByTitle(@PathVariable int id) {

		try {

			return ResponseEntity.ok(service.getAssetsByTitleId(id));

		} catch (RuntimeException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

// CREATE Asset

	@PostMapping("/assets")

	public ResponseEntity<?> createAsset(@Valid @RequestBody Asset asset) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.createAsset(asset));
	}

// UPDATE Asset

	@PutMapping("/assets/{id}")

	public ResponseEntity<?> updateAsset(@PathVariable int id, @Valid @RequestBody Asset asset) {

		try {

			return ResponseEntity.ok(service.updateAsset(id, asset));

		} catch (AssetNotFoundException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

//  DELETE Asset

	@DeleteMapping("/assets/{id}")

	public ResponseEntity<?> deleteAsset(@PathVariable int id) {

		try {

			service.deleteAsset(id);

			return ResponseEntity.ok("Asset deleted successfully");

		} catch (AssetNotFoundException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

}
