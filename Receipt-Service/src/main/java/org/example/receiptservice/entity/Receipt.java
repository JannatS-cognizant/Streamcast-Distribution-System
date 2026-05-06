package org.example.receiptservice.entity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "receipt")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptId;

    @Column(name = "manifest_id", nullable = false)
    private Long manifestId;

    private LocalDate receivedAt;
    private String receivedBy;
    private String receiptURI;
    private String status;
}
