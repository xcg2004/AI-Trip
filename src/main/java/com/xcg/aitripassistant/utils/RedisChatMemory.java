package com.xcg.aitripassistant.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
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
                        //redisTemplate.opsForZSet().add(key,str, System.currentTimeMillis());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).toList();
     //   list.forEach(System.out::println);
        list.forEach(s -> {
                Boolean add = redisTemplate.opsForZSet().add(key, s, System.currentTimeMillis());
                /*if(add.equals(Boolean.TRUE)){
                    System.out.println("添加成功");
                }else{
                    System.out.println("添加失败");
                }*/
        });
        //滑动窗口->控制ZSet存储消息的总数为100
        //0，1，2...100,原理是从0->倒数第101个，确保保留100条最近消息(消息默认按score升序排序)
        Long zSetSize = redisTemplate.opsForZSet().size(key);
        if (zSetSize != null && zSetSize > 100) {
            // 删除前 (size - 100) 条，保留最新的 100 条
            //size = 105条，则删除0~4，还剩100条
            redisTemplate.opsForZSet().removeRange(key, 0, zSetSize - 101);
        }

        //   redisTemplate.opsForList().rightPushAll(key, list);

    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        String key = CHAT_MEMORY_KEY + conversationId;
    //    List<String> messages = redisTemplate.opsForList().range(key, 0, lastN);
        Set<String> messages = redisTemplate.opsForZSet().range(key, 0, lastN);
        if (messages == null) {
            return List.of();
        }
        /*messages.forEach(m->{
            System.out.println("查redis得到："+m);
        });*/
        // 确保Message对象可以被正确反序列化
        List<Message> list = messages.stream()
                .map(m -> {
                    try {
                        return objectMapper.readValue(m, Msg.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).map(Msg::toMessage)
                .toList();
        /*list.forEach(message -> {
            System.out.println("解析出来的消息内容："+message.getText());
        });*/
        return list;
    }

    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(CHAT_MEMORY_KEY + conversationId);
    }
}