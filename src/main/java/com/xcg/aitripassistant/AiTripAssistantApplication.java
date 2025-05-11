package com.xcg.aitripassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "*")
public class AiTripAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiTripAssistantApplication.class, args);
    }

}
