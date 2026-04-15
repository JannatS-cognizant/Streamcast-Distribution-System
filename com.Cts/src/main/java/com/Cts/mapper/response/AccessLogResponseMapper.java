package com.Cts.mapper.response;

import com.Cts.dto.response.AccessLogResponseDTO;
import com.Cts.entity.AccessLog;

public class AccessLogResponseMapper {
    public static AccessLogResponseDTO toDTO(AccessLog log) {

        AccessLogResponseDTO dto = new AccessLogResponseDTO();

        dto.setLogId(log.getLogId());
        dto.setAccessedAt(String.valueOf(log.getAccessedAt()));
        dto.setAction(log.getAction());
        dto.setPartnerId(log.getPartner().getPartnerId());

        return dto;
    }
}
