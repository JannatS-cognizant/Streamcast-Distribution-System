package org.example.partnerservice.dto.request;
import lombok.Data;

@Data
public class PartnerRequestDTO {

    private String name;
    private String contactInfo;
    private String endpointDetailsNote;
    private String status;
}
