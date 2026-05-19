package org.example.partnerservice.service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.partnerservice.client.ContentCatalogClient;
import org.example.partnerservice.entity.Partner;
import org.example.partnerservice.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerService {


    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private ContentCatalogClient client;


   @CircuitBreaker(name="PartnerService",fallbackMethod = "fallbackPartner")
    //fallback method
    public String getPartnerDetails(Long id) {
        return "Fallback response: Partner service is down";
    }
    public Partner createPartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    public List<Partner> getAllPartner(){
        return partnerRepository.findAll();
    }

    public Partner savePartner(Partner partner){
       Object title= client.getTitleById((int) partner.getTitleId());
       System.out.println(title);
       return partnerRepository.save(partner);
    }
}
