package com.cts.StreamCast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class StreamCastApplication {

	public static void main(String[] args) {

		SpringApplication.run(StreamCastApplication.class, args);
	}

}
