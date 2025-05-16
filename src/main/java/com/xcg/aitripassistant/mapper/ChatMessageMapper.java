package com.xcg.aitripassistant.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcg.aitripassistant.domain.po.ChatMessage;

import java.util.List;

public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    void saveBatch(List<ChatMessage> messages);
}
