package com.xcg.aitripassistant.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xcg.aitripassistant.domain.po.ChatMessage;
import com.xcg.aitripassistant.utils.Result;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatService extends IService<ChatMessage> {

    Result<List<ChatMessage>> query(String chatId, String createAt);
}
