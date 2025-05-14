package com.xcg.aitripassistant.propertity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@ConfigurationProperties(prefix = "jwt.properties")
@Component
public class JwtProperties {
    private String tokenName;

    private String secretKey;

    private Long expired;
    public JwtProperties(){

    }
}
