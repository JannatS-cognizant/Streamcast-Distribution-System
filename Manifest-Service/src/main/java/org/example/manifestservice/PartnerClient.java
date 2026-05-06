package org.example.manifestservice;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "Partner-Service")
public interface PartnerClient {
}
