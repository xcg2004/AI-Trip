package com.xcg.aitripassistant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xcg.aitripassistant.domain.po.ChatMessage;
import com.xcg.aitripassistant.mapper.ChatMessageMapper;
import com.xcg.aitripassistant.service.ChatService;
import com.xcg.aitripassistant.utils.RedisParser;
import com.xcg.aitripassistant.utils.Result;
import com.xcg.aitripassistant.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatService {

    private static final String PREFIX = "chatId:";

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private RedisParser redisParser;

    /**
     * 如果没传时间戳，则查询redis所有记录（<=100）
     * 否则查询数据库，分页查询+复合索引优化sql
     * @param chatId 会话id（sessionId）
     * @param createdAt 创建时间
     * @return
     */
    @Override
    public Result<List<ChatMessage>> query(
            String chatId,
            @RequestParam(required = false) String createdAt) {
//        ObjectMapper mapper = new ObjectMapper();
        //前端没有传时间戳
        if(createdAt == null){
            //查redis所有记录，不会影响性能
            String key = PREFIX+chatId;

            //   List<String> list = redisTemplate.opsForList().range(key, 0, -1);
            List<ChatMessage> messages = redisParser.parse(key);

            return Result.success(messages);
        }
        //如果存在时间戳，则查询数据库
        //分页查询10条，创建时间<createdAt
        List<ChatMessage> chatMessages = chatMessageMapper.selectList(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getSessionId, chatId)
                .lt(ChatMessage::getCreatedAt, createdAt)
                .orderByDesc(ChatMessage::getCreatedAt)
                .last("limit 10")

        );
        return Result.success(chatMessages);
    }
}
