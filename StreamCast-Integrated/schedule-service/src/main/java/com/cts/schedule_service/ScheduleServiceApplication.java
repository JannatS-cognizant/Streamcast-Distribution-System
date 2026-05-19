package com.cts.schedule_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.cts.schedule_service.Feign")
@EnableDiscoveryClient
@SpringBootApplication
public class    ScheduleServiceApplication {

    public static void main(String[] args) {
       SpringApplication.run(ScheduleServiceApplication.class, args);
    }

}
 