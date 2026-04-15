package com.Cts.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Entity
@Getter
@Setter
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long receiptId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate receivedAt;

    @NotBlank(message = "Recieved By is required")
    private String receivedBy;

    @NotBlank(message = "Receipt URL is required")
    @Pattern(regexp = "^(http|https)://.*$",message = "Must be a Valid Url")
    private String receiptURI;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "RECEIVED|PENDING",message = "Invalid Status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "manifest_id")
    private Manifest manifest;

}

