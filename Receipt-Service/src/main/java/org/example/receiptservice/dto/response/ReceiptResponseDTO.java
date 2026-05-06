package org.example.receiptservice.dto.response;

import lombok.Data;

@Data
public class ReceiptResponseDTO {
    private Long receiptId;
    private String receivedAt;
    private String receivedBy;
    private String receiptURI;
    private String status;
    private Long manifestId;
}
