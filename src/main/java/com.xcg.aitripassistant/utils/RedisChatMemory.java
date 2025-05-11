package com.xcg.aitripassistant.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;

public class RedisChatMemory implements ChatMemory {
    private static final String CHAT_MEMORY_KEY = "chatId:";
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void add(String conversationId, List<Message> messages) {
        String key = CHAT_MEMORY_KEY + conversationId;
        List<String> list = messages.stream().map(Msg::new).map(msg -> {
                    try {
                        return objectMapper.writeValueAsString(msg);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).toList();

        redisTemplate.opsForList().rightPushAll(key, list);

    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        String key = CHAT_MEMORY_KEY + conversationId;
        List<String> messages = redisTemplate.opsForList().range(key, 0, lastN);

        if (messages == null) {
            return List.of();
        }
        // 确保Message对象可以被正确反序列化
        return messages.stream()
                .map(m->{
                    try {
                        return objectMapper.readValue(m, Msg.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).map(Msg::toMessage)
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(CHAT_MEMORY_KEY + conversationId);
    }
}