package org.example.clauseservice.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "clauses")
@Getter
@Setter
@Data
public class Clause {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clauseId;

    private String clauseType;

    @Column(columnDefinition = "TEXT")
    private String detailsJSON;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    // ✅ NO ContractClient reference here
}