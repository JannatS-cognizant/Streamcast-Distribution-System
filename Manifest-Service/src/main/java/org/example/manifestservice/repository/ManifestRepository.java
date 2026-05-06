package org.example.manifestservice.repository;
import org.example.manifestservice.entity.Manifest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManifestRepository extends JpaRepository<Manifest,Long> {

    List<Manifest> findByPartnerId(Long partnerId);
}
