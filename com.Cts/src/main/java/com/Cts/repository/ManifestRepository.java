package com.Cts.repository;
import com.Cts.entity.Manifest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManifestRepository extends JpaRepository<Manifest,Long> {

    List<Manifest> findByPartnerPartnerId(Long partnerId);
}
