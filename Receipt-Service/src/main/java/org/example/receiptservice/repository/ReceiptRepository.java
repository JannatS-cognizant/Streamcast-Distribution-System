package org.example.receiptservice.repository;

import org.example.receiptservice.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt,Long> {
}
