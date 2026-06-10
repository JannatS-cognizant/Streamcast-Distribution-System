package com.cts.schedule_service.Feign.Title;

import lombok.Data;

@Data
public class CatalogTitleDTO {
    private Long id;
    private String name;
    private String status;
    private String genre;
    private String language;
}
