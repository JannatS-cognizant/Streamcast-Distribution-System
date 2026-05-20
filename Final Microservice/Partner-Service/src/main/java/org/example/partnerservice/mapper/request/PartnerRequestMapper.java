package org.example.partnerservice.mapper.request;

import org.example.partnerservice.dto.request.PartnerRequestDTO;
import org.example.partnerservice.entity.Partner;

public class PartnerRequestMapper {
    public static Partner toEntity(PartnerRequestDTO dto) {

        Partner p = new Partner();

        p.setName(dto.getName());
        p.setContactInfo(dto.getContactInfo());
        p.setEndpointDetailsNote(dto.getEndpointDetailsNote());
        p.setStatus(dto.getStatus());
        p.setTitleId(dto.getTitleId());

        return p;
    }
}
