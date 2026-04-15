package com.Cts.service;
import com.Cts.entity.Partner;
import com.Cts.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerService {


    @Autowired
    private PartnerRepository partnerRepository;


    public Partner createPartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    public List<Partner> getAllPartner(){
        return partnerRepository.findAll();
    }
}
