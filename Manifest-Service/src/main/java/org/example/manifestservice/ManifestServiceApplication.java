package org.example.manifestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ManifestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManifestServiceApplication.class, args);
    }

}
