package com.cts.distribution.controller;
 
import com.cts.distribution.service.DistributionService;
import com.cts.distribution.dto.*;
import com.cts.distribution.entity.DeliveryAttempt;
import com.cts.distribution.entity.Manifest;
import com.cts.distribution.entity.Receipt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
 
@RestController
@RequestMapping("/manifests")
public class DistributionController {
 
	private final DistributionService service;

	public DistributionController(DistributionService service) {
	    this.service = service;
	}
 
    @PreAuthorize("hasAnyRole('ADMIN','DISTRIBUTION_OPERATOR')")
    @PostMapping
    public Manifest create(@RequestBody ManifestDTO dto) {
        return service.createManifest(dto);
    }
 
    @PreAuthorize("hasAnyRole('ADMIN','DISTRIBUTION_OPERATOR','PARTNER_ADMIN')")
    @GetMapping
    public List<Manifest> getAll() {
        return service.getAll();
    }
 
    @PreAuthorize("hasAnyRole('ADMIN','DISTRIBUTION_OPERATOR','PARTNER_ADMIN')")
    @GetMapping("/{id}")
    public Manifest getById(@PathVariable Long id) {
        return service.getById(id);
    }
 
    @PreAuthorize("hasAnyRole('ADMIN','DISTRIBUTION_OPERATOR')")
    @PutMapping("/{id}")
    public Manifest update(@PathVariable Long id, @RequestBody ManifestDTO dto) {
        return service.updateManifest(id, dto);
    }
 
    @PreAuthorize("hasAnyRole('ADMIN','DISTRIBUTION_OPERATOR')")
    @PostMapping("/{id}/attempts")
    public String attempt(@PathVariable Long id, @RequestBody AttemptDTO dto) {
        service.addAttempt(id, dto);
        return "Attempt recorded";
    }
 
    @PreAuthorize("hasAnyRole('ADMIN','DISTRIBUTION_OPERATOR','PARTNER_ADMIN')")
    @PostMapping("/{id}/receipt")
    public String receipt(@PathVariable Long id, @RequestBody ReceiptDTO dto) {
        service.addReceipt(id, dto);
        return "Receipt recorded";
    }
    
    
    
    @PreAuthorize("hasAnyRole('ADMIN','DISTRIBUTION_OPERATOR','PARTNER_ADMIN')")
    @GetMapping("/partner/{partnerId}")
    public List<Manifest> getByPartner(@PathVariable Long partnerId) {
        return service.getByPartner(partnerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','DISTRIBUTION_OPERATOR','PARTNER_ADMIN')")
    @GetMapping("/{id}/receipts")
    public List<Receipt> getReceipts(@PathVariable Long id) {
        return service.getReceiptsByManifest(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','DISTRIBUTION_OPERATOR','PARTNER_ADMIN')")
    @GetMapping("/{id}/attempts")
    public List<DeliveryAttempt> getAttempts(@PathVariable Long id) {
        return service.getAttemptsByManifest(id);
    }
}
