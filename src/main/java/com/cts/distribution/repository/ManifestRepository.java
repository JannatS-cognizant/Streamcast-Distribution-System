package com.cts.distribution.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.distribution.entity.Manifest;

public interface ManifestRepository extends JpaRepository<Manifest, Long> {

	 List<Manifest> findByPartnerId(Long partnerId);
}
