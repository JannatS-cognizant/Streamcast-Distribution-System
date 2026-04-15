package com.Cts.controller;
import com.Cts.api.APIResponse;
import com.Cts.dto.request.PartnerRequestDTO;
import com.Cts.dto.response.PartnerResponseDTO;
import com.Cts.entity.Partner;
import com.Cts.mapper.request.PartnerRequestMapper;
import com.Cts.mapper.response.PartnerResponseMapper;
import com.Cts.service.PartnerService;
import jakarta.validation.Valid;

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
