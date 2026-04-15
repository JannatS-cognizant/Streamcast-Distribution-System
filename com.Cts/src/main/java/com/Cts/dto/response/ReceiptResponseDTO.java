package com.Cts.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReceiptResponseDTO {
    private Long receiptId;
    private String receivedAt;
    private String receivedBy;
    private String receiptURI;
    private String status;
    private Long manifestId;
}
