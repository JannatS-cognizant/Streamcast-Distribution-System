package org.example.manifestservice.controller;

import org.example.manifestservice.api.APIResponse;
import org.example.manifestservice.dto.request.AccessLogRequestDTO;
import org.example.manifestservice.dto.request.ManifestRequestDTO;
import org.example.manifestservice.dto.response.ManifestResponseDTO;
import org.example.manifestservice.entity.Manifest;
import org.example.manifestservice.mapper.request.ManifestRequestMapper;
import org.example.manifestservice.mapper.response.ManifestResponseMapper;
import org.example.manifestservice.service.ManifestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manifests")
public class ManifestController {

    @Autowired
    private ManifestService manifestService;

    // ✅ CREATE
    @PostMapping
    public APIResponse<ManifestResponseDTO> createManifest(
            @RequestBody ManifestRequestDTO dto) {

        Manifest manifest = ManifestRequestMapper.toEntity(dto);
        Manifest saved = manifestService.createManifest(manifest);

        return new APIResponse<>(
                "Manifest created",
                ManifestResponseMapper.toDTO(saved),
                true
        );
    }

    // ✅ GET ALL
    @GetMapping
    public APIResponse<List<ManifestResponseDTO>> getAll() {

        List<ManifestResponseDTO> list = manifestService.getAllManifest()
                .stream()
                .map(ManifestResponseMapper::toDTO)
                .toList();

        return new APIResponse<>("All manifests", list, true);
    }

    // ✅ GET BY PARTNER
    @GetMapping("/partner/{partnerId}")
    public APIResponse<List<ManifestResponseDTO>> getByPartner(
            @PathVariable Long partnerId) {

        List<ManifestResponseDTO> list = manifestService.getManifestByPartner(partnerId)
                .stream()
                .map(ManifestResponseMapper::toDTO)
                .toList();

        return new APIResponse<>("Partner manifests", list, true);
    }

    // ✅ ADD ATTEMPT
    @PostMapping("/{id}/attempts")
    public ResponseEntity<APIResponse<String>> addAttempt(
            @PathVariable Long id,
            @RequestBody AccessLogRequestDTO requestDTO) {

        manifestService.addAttempt(id, requestDTO);

        return ResponseEntity.ok(
                new APIResponse<>("Attempt added successfully", null, true)
        );
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<String>> updateManifest(
            @PathVariable Long id,
            @RequestBody ManifestRequestDTO dto) {

        manifestService.updateManifest(id, dto);

        return ResponseEntity.ok(
                new APIResponse<>("Manifest updated successfully", null, true)
        );
    }

    // ✅ GET BY ID (Feign endpoint)
    @GetMapping("/{id}")
    public ResponseEntity<ManifestResponseDTO> getManifestById(
            @PathVariable Long id) {

        Manifest manifest = manifestService.getManifestById(id);

        return ResponseEntity.ok(
                ManifestResponseMapper.toDTO(manifest)
        );
    }
}