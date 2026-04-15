package com.Cts.service;

import com.Cts.entity.ComplianceCheck;
import java.util.List;

public interface ComplianceService {

    ComplianceCheck runCheck(ComplianceCheck check);
    ComplianceCheck getCheckById(Long checkId);
    List<ComplianceCheck> getAllChecks();
    ComplianceCheck updateCheck(Long checkId, ComplianceCheck check);
}
