package org.example.partnerservice.repository;
import org.example.partnerservice.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner,Long> {
}
