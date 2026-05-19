package com.cts.StreamCast.Controller;

import com.cts.StreamCast.dto.AssetDTO;
import com.cts.StreamCast.dto.AssetDTORequest;
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

import com.cts.StreamCast.Entity.Asset;
import com.cts.StreamCast.Exception.AssetNotFoundException;
import com.cts.StreamCast.Service.AssetService;

import jakarta.validation.Valid;

import java.util.List;

@RestController

@RequestMapping("/api")

@CrossOrigin

public class AssetController {

	@Autowired

	private AssetService service;

// GET Assets by Title ID

	@PreAuthorize("hasAnyRole('ADMIN','CONTENT_OWNER','RIGHTS_MANAGER','SCHEDULER')")
	@GetMapping("/titles/{id}/assets")

	public ResponseEntity<?> getAssetsByTitle( @PathVariable Integer id) {

		List<AssetDTO> assets = service.getAssetsByTitleId(id);

		if (assets.isEmpty()) {
			throw new AssetNotFoundException("No assets found for title " + id);
		}

		return ResponseEntity.ok(assets);

	}

// CREATE Asset

	@PreAuthorize("hasAnyRole('ADMIN','CONTENT_OWNER')")
	@PostMapping("/assets/{titleId}")

	public ResponseEntity<?> createAsset(@PathVariable Integer titleId,@Valid @RequestBody AssetDTORequest dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.createAsset(titleId,dto));
	}

// UPDATE Asset

	@PreAuthorize("hasAnyRole('ADMIN','CONTENT_OWNER')")
	@PutMapping("/assets/{id}")

	public ResponseEntity<?> updateAsset(@PathVariable Integer id, @Valid @RequestBody AssetDTORequest asset) {

		try {

			return ResponseEntity.ok(service.updateAsset(id, asset));

		} catch (AssetNotFoundException e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		}

	}

//  DELETE Asset

	@PreAuthorize("hasRole('ADMIN')")
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
