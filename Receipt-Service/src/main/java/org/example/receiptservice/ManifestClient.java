package org.example.receiptservice;

import org.example.receiptservice.dto.response.ManifestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Manifest-Service", url = "http://localhost:8083")
public interface ManifestClient {

    @GetMapping("/manifests/{id}")
    ManifestDTO getManifestById(@PathVariable("id") Long id);
}