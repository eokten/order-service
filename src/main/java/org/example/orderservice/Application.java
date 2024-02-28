package org.example.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.example.event.api", "org.example.orderservice"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}