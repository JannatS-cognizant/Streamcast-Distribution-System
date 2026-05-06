package org.example.manifestservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.example.manifestservice.dto.request.AccessLogRequestDTO;
import org.example.manifestservice.dto.request.ManifestRequestDTO;
import org.example.manifestservice.entity.Manifest;
import org.example.manifestservice.repository.ManifestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ManifestService {

    @Autowired
    private ManifestRepository manifestRepository;

    private static final String CB_NAME = "ManifestService";

    // ✅ CREATE
    public Manifest createManifest(Manifest manifest) {
        return manifestRepository.save(manifest);
    }

    // ✅ GET ALL
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "getAllManifestFallback")
    @Retry(name = CB_NAME)
    public List<Manifest> getAllManifest() {
        return manifestRepository.findAll();
    }

    public List<Manifest> getAllManifestFallback(Throwable ex) {
        return Collections.emptyList();
    }

    // ✅ GET BY PARTNER
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "getManifestByPartnerFallback")
    @Retry(name = CB_NAME)
    public List<Manifest> getManifestByPartner(Long partnerId) {
        return manifestRepository.findByPartnerId(partnerId);
    }

    public List<Manifest> getManifestByPartnerFallback(Long partnerId, Throwable ex) {
        return Collections.emptyList();
    }

    // ✅ GET BY ID (Feign‑safe)
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "getManifestByIdFallback")
    @Retry(name = CB_NAME)
    public Manifest getManifestById(Long id) {
        return manifestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manifest not found"));
    }

    // ✅ FALLBACK (DIFFERENT NAME ✅)
    public Manifest getManifestByIdFallback(Long id, Throwable ex) {
        return null;
    }

    // ✅ ADD ATTEMPT
    public void addAttempt(Long id, AccessLogRequestDTO dto) {
        manifestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manifest not found"));
    }

    // ✅ UPDATE
    public void updateManifest(Long id, ManifestRequestDTO dto) {

        Manifest existing = manifestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manifest not found"));

        if (dto.getTitleId() != null) {
            existing.setStatus(dto.getTitleId());
        }

        manifestRepository.save(existing);
    }
}