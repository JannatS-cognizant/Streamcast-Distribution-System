package org.example.partnerservice.controller;
import org.example.partnerservice.api.APIResponse;
import org.example.partnerservice.dto.request.PartnerRequestDTO;
import org.example.partnerservice.dto.response.PartnerResponseDTO;
import org.example.partnerservice.entity.Partner;
import org.example.partnerservice.mapper.request.PartnerRequestMapper;
import org.example.partnerservice.mapper.response.PartnerResponseMapper;
import org.example.partnerservice.service.PartnerService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partners")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    @PostMapping
    public APIResponse<PartnerResponseDTO> createPartner(
            @RequestBody PartnerRequestDTO dto) {

        Partner partner = PartnerRequestMapper.toEntity(dto);
        Partner saved = partnerService.createPartner(partner);

        return new APIResponse<>("Partner created",
                PartnerResponseMapper.toDTO(saved), true);
    }

    @GetMapping
    public APIResponse<List<PartnerResponseDTO>> getAllPartners() {

        List<PartnerResponseDTO> list = partnerService.getAllPartner()
                .stream()
                .map(PartnerResponseMapper::toDTO)
                .toList();

        return new APIResponse<>("Partners fetched", list, true);
    }
}
