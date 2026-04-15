package com.Cts.mapper.request;

import com.Cts.dto.request.AccessLogRequestDTO;
import com.Cts.entity.AccessLog;

import java.time.LocalDate;

public class AccessLogRequestMapper {
    public static AccessLog toEntity(AccessLogRequestDTO dto) {

        AccessLog log = new AccessLog();

        log.setAccessedAt(LocalDate.parse(dto.getAccessedAt()));
        log.setAction(dto.getAction());

        return log;
    }

}
