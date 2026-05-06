package org.example.contractservice.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
@Getter
@Setter
@Entity
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @NotBlank(message = "Title Id is required")
    private String titleId;

    @NotBlank(message = "Territory List is required")
    private String territoryListJson;

    @NotNull(message = "Start Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Exclusivity Flag is required")
    private Boolean exclusivityFlag;

    @NotBlank(message = "Terms Summary is required")
    @Size(min = 5,max = 200)
    private String termsSummary;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|INACTIVE",message = "Status must be ACTIVE or INACTIVE")
    private String status;

    @AssertTrue(message = "End date must be after start date")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate);
    }

}
