package com.xcg.aitripassistant;

import com.xcg.aitripassistant.propertity.JwtProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "*")
@EnableConfigurationProperties(JwtProperties.class)
@MapperScan(basePackages = "com.xcg.aitripassistant.mapper")
public class AiTripAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiTripAssistantApplication.class, args);
    }

}
