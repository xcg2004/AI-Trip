package com.xcg.aitripassistant.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        // 设置key的序列化方式为String
        template.setKeySerializer(new StringRedisSerializer());

        // 设置value的序列化方式为Jackson
        template.setValueSerializer(new StringRedisSerializer());
        // 设置连接工厂
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}