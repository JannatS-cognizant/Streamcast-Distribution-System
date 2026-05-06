package org.example.receiptservice.service;

import org.example.receiptservice.ManifestClient;
import org.example.receiptservice.dto.response.ManifestDTO;
import org.example.receiptservice.entity.Manifest;
import org.example.receiptservice.entity.Receipt;
import org.example.receiptservice.repository.ManifestRepository;
import org.example.receiptservice.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ManifestClient manifestClient;

    public Receipt saveReceipt(Receipt receipt, Long manifestId) {

        if (manifestId == null) {
            throw new IllegalArgumentException("manifestId must not be null");
        }

        // ✅ Remote service call
        ManifestDTO manifestDTO = manifestClient.getManifestById(manifestId);

        if (manifestDTO == null) {
            throw new RuntimeException("Manifest not found with id: " + manifestId);
        }

        // ✅ Store ONLY the ID (NO JPA relation)
        receipt.setManifestId(manifestId);

        return receiptRepository.save(receipt);
    }
}
