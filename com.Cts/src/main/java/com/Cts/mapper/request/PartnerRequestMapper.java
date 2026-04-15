package com.Cts.mapper.request;

import com.Cts.dto.request.PartnerRequestDTO;
import com.Cts.entity.Partner;

public class PartnerRequestMapper {
    public static Partner toEntity(PartnerRequestDTO dto) {

        Partner p = new Partner();

        p.setName(dto.getName());
        p.setContactInfo(dto.getContactInfo());
        p.setEndpointDetailsNote(dto.getEndpointDetailsNote());
        p.setStatus(dto.getStatus());

        return p;
    }
}
