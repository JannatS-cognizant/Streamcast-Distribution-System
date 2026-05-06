package org.example.receiptservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ReceiptRequestDTO {

    @NotBlank(message = "Received date is required")
    @Pattern(
            regexp = "\\d{4}-\\d{2}-\\d{2}",
            message = "receivedAt must be in yyyy-MM-dd format"
    )
    private String receivedAt;

    @NotBlank(message = "Received by is required")
    private String receivedBy;

    @NotBlank(message = "Receipt URL is required")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "Must be a valid URL"
    )
    private String receiptURI;

    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "RECEIVED|PENDING",
            message = "Status must be RECEIVED or PENDING"
    )
    private String status;

    @NotNull(message = "manifestId is required")
    private Long manifestId;
}