package org.example.receiptservice.mapper.request;

import org.example.receiptservice.dto.request.ReceiptRequestDTO;
import org.example.receiptservice.entity.Manifest;
import org.example.receiptservice.entity.Receipt;

import java.time.LocalDate;

public class ReceiptRequestMapper {
    public static Receipt toEntity(ReceiptRequestDTO dto) {

        Receipt r = new Receipt();

        r.setReceivedAt(LocalDate.parse(dto.getReceivedAt()));
        r.setReceivedBy(dto.getReceivedBy());
        r.setReceiptURI(dto.getReceiptURI());
        r.setStatus(dto.getStatus());

        Manifest m = new Manifest();
        m.setManifestId(dto.getManifestId());

        r.setManifestId(m.getManifestId());

        return r;
    }

}
