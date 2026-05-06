package org.example.receiptservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "manifest")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
public class Manifest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long manifestId;

    public Long getManifestId() {
        return manifestId;
    }

    public void setManifestId(Long manifestId) {
        this.manifestId = manifestId;
    }
}