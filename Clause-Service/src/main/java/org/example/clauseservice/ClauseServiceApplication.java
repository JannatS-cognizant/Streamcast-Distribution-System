package org.example.clauseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ClauseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClauseServiceApplication.class, args);
    }

}
