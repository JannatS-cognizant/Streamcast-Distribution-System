package com.Cts.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Clause {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long clauseId;

    @NotBlank(message = "Clause Type is required")
    private String clauseType;

    @NotBlank(message = "Details is required")
    private String detailsJSON;

    @NotNull(message = "Effective from is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveFrom;

    @NotNull(message = "Effective To is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveTo;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @AssertTrue(message = "EffectiveTo date must be after EffectiveFrom date")
    public boolean isDateRangeValid() {
        if (effectiveFrom == null || effectiveTo == null) {
            return true;
        }
        return effectiveTo.isAfter(effectiveFrom);
    }
}
