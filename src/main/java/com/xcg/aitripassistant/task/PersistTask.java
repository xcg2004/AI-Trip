package com.xcg.aitripassistant.task;

import com.xcg.aitripassistant.domain.po.ChatMessage;
import com.xcg.aitripassistant.mapper.ChatMessageMapper;
import com.xcg.aitripassistant.utils.RedisParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 定时任务，每隔10分钟将redis持久化到mysql
 */
@Component
@Slf4j
public class PersistTask {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private RedisParser redisParser;
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void persist() {

        log.info("开始持久化");
        //
        Set<String> keys = redisTemplate.keys("chatId:*");
        if (keys == null) {
            return;
        }
        List<String> list = keys.stream().toList();

        for (String key : list) {
            // 从redis中取出数据
            List<ChatMessage> messages = redisParser.parse(key);
            //批量插入
            //chatMessageMapper.insert(messages);
            chatMessageMapper.saveBatch(messages);
        }
    }



}
