package com.Cts.controller;
import com.Cts.api.APIResponse;
import com.Cts.dto.request.AccessLogRequestDTO;
import com.Cts.dto.request.ManifestRequestDTO;
import com.Cts.dto.response.ManifestResponseDTO;
import com.Cts.entity.Manifest;
import com.Cts.mapper.request.ManifestRequestMapper;
import com.Cts.mapper.response.ManifestResponseMapper;
import com.Cts.service.ManifestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/manifests")
public class ManifestController {

    @Autowired
    private ManifestService manifestService;

    @PostMapping
    public APIResponse<ManifestResponseDTO> createManifest(
            @RequestBody ManifestRequestDTO dto) {

        Manifest manifest = ManifestRequestMapper.toEntity(dto);
        Manifest saved = manifestService.createManifest(manifest);

        return new APIResponse<>("Manifest created",
                ManifestResponseMapper.toDTO(saved), true);
    }

    @GetMapping
    public APIResponse<List<ManifestResponseDTO>> getAll() {

        List<ManifestResponseDTO> list = manifestService.getAllManifest()
                .stream()
                .map(ManifestResponseMapper::toDTO)
                .toList();

        return new APIResponse<>("All manifests", list, true);
    }

    @GetMapping("/partner/{partnerId}")
    public APIResponse<List<ManifestResponseDTO>> getByPartner(
            @PathVariable Long partnerId) {

        List<ManifestResponseDTO> list = manifestService.getManifestByPartner(partnerId)
                .stream()
                .map(ManifestResponseMapper::toDTO)
                .toList();

        return new APIResponse<>("Partner manifests", list, true);
    }



        @PostMapping("/{id}/attempts")
        public ResponseEntity<APIResponse<String>> addAttempt(
                @PathVariable Long id,
                @RequestBody AccessLogRequestDTO requestDTO) {

            manifestService.addAttempt(id, requestDTO);

            return ResponseEntity.ok(
                    new APIResponse<>("Attempt added successfully", null, true)
            );
        }

        @PutMapping("/{id}")
        public ResponseEntity<APIResponse<String>> updateManifest(
                @PathVariable Long id,
                @RequestBody ManifestRequestDTO dto) {

            manifestService.updateManifest(id, dto);

            return ResponseEntity.ok(
                    new APIResponse<>("Manifest updated successfully", null, true)
            );
        }

}