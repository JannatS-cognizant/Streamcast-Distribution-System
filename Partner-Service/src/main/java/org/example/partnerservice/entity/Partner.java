package org.example.partnerservice.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long partnerId;

    @NotBlank(message = "Name is required")
    @Size(min=2,max = 50)
    private String name;

    @NotBlank(message = "Contact is required")
    @Pattern(regexp = "^[0-9]{10}$",message = "Must be a Valid 10-digit phone number")
    private String contactInfo;

    @NotBlank(message = "Endpoint Details Note is required")
    private String endpointDetailsNote;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|INACTIVE",message = "Status must be ACTIVE or INACTIVE")
    private String status;

}
