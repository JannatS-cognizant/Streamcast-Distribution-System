package org.example.partnerservice.mapper.response;

import org.example.partnerservice.dto.response.PartnerResponseDTO;
import org.example.partnerservice.entity.Partner;

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
