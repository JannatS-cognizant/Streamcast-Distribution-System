package com.cts.distribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.distribution.entity.DeliveryAttempt;

public interface AttemptRepository extends JpaRepository<DeliveryAttempt, Long> {

}
