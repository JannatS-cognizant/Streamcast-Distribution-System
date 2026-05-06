package org.example.receiptservice.repository;

import org.example.receiptservice.entity.Manifest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManifestRepository extends JpaRepository<Manifest, Long> {

}
