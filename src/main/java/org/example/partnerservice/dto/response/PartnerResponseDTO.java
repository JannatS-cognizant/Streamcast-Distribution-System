package org.example.partnerservice.dto.response;

import lombok.Data;

@Data
public class PartnerResponseDTO {

    private Long partnerId;
    private String name;
    private String contactInfo;
    private String endpointDetailsNote;
    private String status;
}
