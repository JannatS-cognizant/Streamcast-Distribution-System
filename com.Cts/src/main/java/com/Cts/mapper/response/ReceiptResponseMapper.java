package com.Cts.mapper.response;

import com.Cts.dto.response.ReceiptResponseDTO;
import com.Cts.entity.Receipt;

public class ReceiptResponseMapper {
    public static ReceiptResponseDTO toDTO(Receipt r) {

        ReceiptResponseDTO dto = new ReceiptResponseDTO();

        dto.setReceiptId(r.getReceiptId());
        dto.setReceivedAt(String.valueOf(r.getReceivedAt()));
        dto.setReceivedBy(r.getReceivedBy());
        dto.setReceiptURI(r.getReceiptURI());
        dto.setStatus(r.getStatus());
        dto.setManifestId(r.getManifest().getManifestId());

        return dto;
    }
}
