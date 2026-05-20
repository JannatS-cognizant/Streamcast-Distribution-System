package com.cts.schedule_service.Feign.Title;

import com.cts.schedule_service.exception.InvalidScheduleException;
import org.springframework.stereotype.Component;

@Component
public class CatalogFeignFallback implements CatalogFeignClient {

    @Override
    public CatalogTitleDTO getTitle(Long id) {
        throw new InvalidScheduleException(
                "Catalog-Service is unavailable. Cannot validate titleId: " + id
        );
    }
}