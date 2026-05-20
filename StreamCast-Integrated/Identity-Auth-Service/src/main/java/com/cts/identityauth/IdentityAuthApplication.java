package com.cts.identityauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableMethodSecurity          // enables @PreAuthorize on controllers
public class IdentityAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdentityAuthApplication.class, args);
    }
}