package com.Cts.mapper.request;

import com.Cts.dto.request.ManifestRequestDTO;
import com.Cts.entity.Manifest;
import com.Cts.entity.Partner;

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

        Partner p = new Partner();
        p.setPartnerId(dto.getPartnerId());

        m.setPartner(p);

        return m;
    }
}
