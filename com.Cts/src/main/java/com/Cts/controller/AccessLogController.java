package com.Cts.controller;
import com.Cts.api.APIResponse;
import com.Cts.dto.request.AccessLogRequestDTO;
import com.Cts.dto.response.AccessLogResponseDTO;
import com.Cts.entity.AccessLog;
import com.Cts.mapper.request.AccessLogRequestMapper;
import com.Cts.mapper.response.AccessLogResponseMapper;
import com.Cts.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/logs")
public class AccessLogController {

    @Autowired
    private AccessLogService accessLogService;

    @PostMapping("/{partnerId}")
    public APIResponse<AccessLogResponseDTO> addLog(
            @PathVariable Long partnerId,
            @RequestBody AccessLogRequestDTO dto) {

        AccessLog log = AccessLogRequestMapper.toEntity(dto);
        AccessLog saved = accessLogService.addLog(partnerId, log);

        return new APIResponse<>("Log created",
                AccessLogResponseMapper.toDTO(saved), true);
    }

    @GetMapping("/partner/{partnerId}")
    public APIResponse<List<AccessLogResponseDTO>> getLogsByPartner(
            @PathVariable Long partnerId) {

        List<AccessLogResponseDTO> list = accessLogService.getLogsByPartner(partnerId)
                .stream()
                .map(AccessLogResponseMapper::toDTO)
                .toList();

        return new APIResponse<>("Logs fetched", list, true);
    }
}

