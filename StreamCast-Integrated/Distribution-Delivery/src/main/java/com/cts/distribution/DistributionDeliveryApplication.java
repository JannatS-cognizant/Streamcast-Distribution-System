package com.cts.distribution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.cts.distribution.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class DistributionDeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributionDeliveryApplication.class, args);
	}

}
