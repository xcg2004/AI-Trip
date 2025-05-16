package com.xcg.aitripassistant.controller;

import com.xcg.aitripassistant.domain.po.ChatMessage;
import com.xcg.aitripassistant.service.ChatService;
import com.xcg.aitripassistant.service.ISessionService;
import com.xcg.aitripassistant.utils.RedisChatMemory;
import com.xcg.aitripassistant.utils.Result;
import com.xcg.aitripassistant.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    @Autowired
    //mcp 客户端，qwen-max-latest，不支持多模态，用于纯文本对话
    private ChatClient mcpChatClient;
    @Autowired
    //qwen-omni,支持多模态对话
    private ChatClient chatClient;

    @Autowired
    //redis存储会话记忆，供 SpringAI AOP 动态代理增强，作为 ChatMemory
    private RedisChatMemory redisChatMemory;

    @Autowired
    //redisTemplate，用于redis操作
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ISessionService sessionService;

    /**
     * 聊天
     * @param chatId 会话id
     * @param question user  question
     * @param files png、mp3多模态
     * @return 流式响应输出
     */
    @PostMapping(value = "/{chatId}",produces = "text/html;charset=utf-8")
    public Flux<String> chat(@PathVariable String chatId,
                             @RequestParam("question") String question,
                             @RequestParam(value = "files",required = false) List<MultipartFile> files
    ){

        if(files == null || files.isEmpty()){
            //纯文本对话
            return textChat(chatId,question);
        }
        //多模态对话
        return mediaChat(chatId,question,files);
    }

    private Flux<String> mediaChat(String chatId, String question, List<MultipartFile> files) {
        Media[] medias = files.stream().map(f -> Media.builder()
                .mimeType(MimeType.valueOf(Objects.requireNonNull(f.getContentType())))
                .data(f.getResource())
                .build()
        ).toArray(Media[]::new);
        log.info("MultiMedia:{}", Arrays.toString(medias));
        return chatClient.prompt()
                .user(p->p.text(question).media(medias))
                .advisors(a->a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,chatId))
                .stream()
                .content();
    }

    private Flux<String> textChat(String chatId, String question) {
         log.info("user:{}",question);
         return mcpChatClient.prompt()
                .user(question)
                .advisors(a->a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,chatId))
                .stream()
                .content();

    }

    @GetMapping
    public Result<List<Long>> getSessionIds(){
        Long userId = UserHolder.getUser();
        log.info("获取：{}用户的所有会话id",userId);
        return sessionService.listIds(userId);
    }


    /**
     * 分页查询聊天记录
     * @param chatId session表的id
     * @param createAt 创建时间，用于DESC，让前端滚动查询
     * @return
     */
    @GetMapping("/history/{chatId}")
    public Result<List<ChatMessage>> getChatHistory(@PathVariable String chatId, String createAt){
        log.info("查询会话：{} 中创建时间 < {}的聊天记录",chatId,createAt);

        return chatService.query(chatId,createAt);
       // return redisChatMemory.get(chatId,100).toString();
    }



}
