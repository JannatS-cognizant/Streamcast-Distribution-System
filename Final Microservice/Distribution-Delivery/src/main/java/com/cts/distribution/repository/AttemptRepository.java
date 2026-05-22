package com.cts.distribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.distribution.entity.DeliveryAttempt;

import java.util.List;

public interface AttemptRepository extends JpaRepository<DeliveryAttempt, Long> {
    List<DeliveryAttempt> findByManifestId(Long manifestId);
}
