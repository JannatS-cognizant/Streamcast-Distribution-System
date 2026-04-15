package com.Cts.controller;
import com.Cts.api.APIResponse;
import com.Cts.dto.request.ReceiptRequestDTO;
import com.Cts.dto.response.ReceiptResponseDTO;
import com.Cts.entity.Receipt;
import com.Cts.mapper.request.ReceiptRequestMapper;
import com.Cts.mapper.response.ReceiptResponseMapper;
import com.Cts.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @PostMapping
    public APIResponse<ReceiptResponseDTO> saveReceipt(
            @RequestBody ReceiptRequestDTO dto) {

        Receipt receipt = ReceiptRequestMapper.toEntity(dto);
        Receipt saved = receiptService.saveReceipt(receipt);

        return new APIResponse<>("Receipt saved",
                ReceiptResponseMapper.toDTO(saved), true);
    }
}

