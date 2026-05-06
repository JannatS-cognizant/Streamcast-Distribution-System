package org.example.receiptservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ReceiptServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReceiptServiceApplication.class, args);
    }
}