package org.example.receiptservice.controller;

import jakarta.validation.Valid;
import org.example.receiptservice.api.APIResponse;
import org.example.receiptservice.dto.request.ReceiptRequestDTO;
import org.example.receiptservice.dto.response.ReceiptResponseDTO;
import org.example.receiptservice.entity.Receipt;
import org.example.receiptservice.mapper.request.ReceiptRequestMapper;
import org.example.receiptservice.mapper.response.ReceiptResponseMapper;
import org.example.receiptservice.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @PostMapping
    public APIResponse<ReceiptResponseDTO> saveReceipt(
            @Valid @RequestBody ReceiptRequestDTO dto) {

        Receipt receipt = ReceiptRequestMapper.toEntity(dto);
        Receipt saved = receiptService.saveReceipt(receipt, dto.getManifestId());

        return new APIResponse<>(
                "Receipt saved",
                ReceiptResponseMapper.toDTO(saved),
                true
        );
    }
}