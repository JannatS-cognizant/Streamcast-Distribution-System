package org.example.manifestservice.mapper.request;

import org.example.manifestservice.dto.request.ManifestRequestDTO;
import org.example.manifestservice.entity.Manifest;

import java.time.LocalDate;

public class ManifestRequestMapper {
    public static Manifest toEntity(ManifestRequestDTO dto) {

        Manifest m = new Manifest();

        m.setTitleId(dto.getTitleId());
        m.setAssetIdsJSON(dto.getAssetIdsJSON());
        m.setDestination(dto.getDestination());
        m.setCreatedBy(dto.getCreatedBy());
        m.setCreatedAt(LocalDate.parse(dto.getCreatedAt()));
        m.setStatus(dto.getStatus());


        m.setPartnerId(dto.getPartnerId());
        return m;
    }
}
