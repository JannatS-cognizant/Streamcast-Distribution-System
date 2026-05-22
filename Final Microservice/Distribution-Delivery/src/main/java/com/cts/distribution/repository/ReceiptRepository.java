package com.cts.distribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.distribution.entity.Receipt;

import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByManifestId(Long manifestId);
}
