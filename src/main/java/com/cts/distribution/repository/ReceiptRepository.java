package com.cts.distribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.distribution.entity.Receipt;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

}
