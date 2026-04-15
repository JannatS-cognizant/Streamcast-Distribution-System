package com.Cts.repository;
import com.Cts.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessLogRepository extends JpaRepository<AccessLog,Long> {
    List<AccessLog> findByPartnerPartnerId(Long partnerId);


}
