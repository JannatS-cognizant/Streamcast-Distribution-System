package com.Cts.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReceiptRequestDTO {

    private String receivedAt;
    private String receivedBy;
    private String receiptURI;
    private String status;
    private Long manifestId;
}
