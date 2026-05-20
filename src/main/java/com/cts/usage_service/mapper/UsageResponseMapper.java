package com.cts.usage_service.mapper;

import com.cts.usage_service.dto.UsageSummaryDTO;
import com.cts.usage_service.dto.UsageDetailsDTO;
import com.cts.usage_service.dto.UsageBreakdownDTO;
import com.cts.usage_service.entity.UsageRecord;

    public class UsageResponseMapper {

        private UsageResponseMapper() {}

        public static UsageDetailsDTO toDetailsDTO(UsageRecord r) {
            UsageDetailsDTO dto = new UsageDetailsDTO();
            dto.titleId = r.getTitleId();
            dto.platform = r.getPlatform();
            dto.date = r.getDate();
            dto.views = r.getViews();
            dto.revenue = r.getRevenue();
            return dto;
        }
    }

