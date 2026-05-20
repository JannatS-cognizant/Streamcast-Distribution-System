package com.cts.streamcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class StreamcastBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamcastBackendApplication.class, args);
	}
}
