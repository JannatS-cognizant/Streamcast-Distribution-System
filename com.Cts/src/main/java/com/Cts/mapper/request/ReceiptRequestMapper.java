package com.Cts.mapper.request;

import com.Cts.dto.request.ReceiptRequestDTO;
import com.Cts.entity.Manifest;
import com.Cts.entity.Receipt;

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

        r.setManifest(m);

        return r;
    }

}
