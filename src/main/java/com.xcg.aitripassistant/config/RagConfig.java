package com.xcg.aitripassistant.config;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {
    @Bean
    public TokenTextSplitter splitter(){
        return new TokenTextSplitter();
    }

}
