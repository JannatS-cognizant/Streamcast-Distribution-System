package com.Cts.service;
import com.Cts.dto.request.AccessLogRequestDTO;
import com.Cts.dto.request.ManifestRequestDTO;
import com.Cts.entity.Manifest;
import com.Cts.repository.ManifestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManifestService {

    @Autowired
    private ManifestRepository manifestRepository;

    public Manifest createManifest(Manifest manifest) {
        return manifestRepository.save(manifest);
    }

    public List<Manifest> getAllManifest(){
        return manifestRepository.findAll();
    }

    public List<Manifest> getManifestByPartner(Long partnerId){
        return manifestRepository.findByPartnerPartnerId(partnerId);
    }



    public void addAttempt(Long id, AccessLogRequestDTO dto) {

        Manifest manifest = manifestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manifest not found"));
    }

    public void updateManifest(Long id, ManifestRequestDTO dto) {

        Manifest existing = manifestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manifest not found"));

        if(dto.getTitleId() != null) {
            existing.setStatus(dto.getTitleId());
        }

        manifestRepository.save(existing);
    }
}
