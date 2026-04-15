package com.Cts.service;
import com.Cts.entity.AccessLog;
import com.Cts.entity.Partner;
import com.Cts.repository.AccessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessLogService {

    @Autowired
    private AccessLogRepository accessLogRepository;

    public List<AccessLog>getLogsByPartner(Long partnerId){
        return accessLogRepository.findByPartnerPartnerId(partnerId);
    }

    public AccessLog addLog(Long partnerId, AccessLog log) {
        Partner partner=new Partner();
        partner.setPartnerId(partnerId);
        log.setPartner(partner);
        return accessLogRepository.save(log);
    }
}
