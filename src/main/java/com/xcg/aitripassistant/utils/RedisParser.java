package com.xcg.aitripassistant.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xcg.aitripassistant.domain.po.ChatMessage;
import com.xcg.aitripassistant.domain.po.RedisJson;
import com.xcg.aitripassistant.domain.po.Session;
import com.xcg.aitripassistant.mapper.SessionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class RedisParser {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private SessionMapper sessionMapper;

    public List<ChatMessage> parse(String key) {
        ObjectMapper mapper  = new ObjectMapper();
        List<ChatMessage> messages = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {

            //ZSet add key score member/value
            //member
            String value = typedTuple.getValue();

            //1.747305966391E12,需要格式化转换
            Double score = typedTuple.getScore();
            long timestampMillis = score.longValue(); // 毫秒时间戳

            // ✅ 使用 Instant.fromEpochMilli() 来正确解析
            LocalDateTime localDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestampMillis),
                    ZoneId.of("Asia/Shanghai")
            );


            //     localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            //解析key，得到chatId->sessionId->查询userId
            String chatId = key.substring(key.indexOf(":")+1);
            Session session = sessionMapper.selectById(chatId);
            /*if(session == null){
                session = Session
                        .builder()
                        .userId(2L)
                        .build();
            }*/
            //userId不能直接从ThreadLocal里拿，因为这个key（会话）不一定是他的
            Long userId = session.getUserId();

            try{
                //反序列化
                RedisJson redisJson = mapper.readValue(value, RedisJson.class);
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setText(redisJson.getText());
                chatMessage.setMessageType(redisJson.getMessageType());
                chatMessage.setCreatedAt(localDateTime);
                chatMessage.setUserId(userId);
                chatMessage.setSessionId(Long.valueOf(chatId));

                messages.add(chatMessage);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return messages;
    }
}


