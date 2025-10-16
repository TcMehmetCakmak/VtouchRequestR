package com.vtouch.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VTouchAuthServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(VTouchAuthServiceApplication.class, args);
    }
}
