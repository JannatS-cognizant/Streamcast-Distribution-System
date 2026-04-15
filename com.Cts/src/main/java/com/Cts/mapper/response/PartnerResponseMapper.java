package com.Cts.mapper.response;

import com.Cts.dto.response.PartnerResponseDTO;
import com.Cts.entity.Partner;

public class PartnerResponseMapper {
    public static PartnerResponseDTO toDTO(Partner p) {

        PartnerResponseDTO dto = new PartnerResponseDTO();

        dto.setPartnerId(p.getPartnerId());
        dto.setName(p.getName());
        dto.setContactInfo(p.getContactInfo());
        dto.setEndpointDetailsNote(p.getEndpointDetailsNote());
        dto.setStatus(p.getStatus());

        return dto;
    }
}
