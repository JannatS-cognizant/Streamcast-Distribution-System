package org.example.manifestservice.mapper.response;

import org.example.manifestservice.dto.response.ManifestResponseDTO;
import org.example.manifestservice.entity.Manifest;

public class ManifestResponseMapper {
    public static ManifestResponseDTO toDTO(Manifest m) {

        ManifestResponseDTO dto = new ManifestResponseDTO();

        dto.setManifestId(m.getManifestId());
        dto.setTitleId(m.getTitleId());
        dto.setAssetIdsJSON(m.getAssetIdsJSON());
        dto.setDestination(m.getDestination());
        dto.setCreatedBy(m.getCreatedBy());
        dto.setCreatedAt(String.valueOf(m.getCreatedAt()));
        dto.setStatus(m.getStatus());
        dto.setPartnerId(m.getPartnerId());

        return dto;
    }
}
