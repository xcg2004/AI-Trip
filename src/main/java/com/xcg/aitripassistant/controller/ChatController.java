package com.xcg.aitripassistant.controller;

import com.xcg.aitripassistant.utils.RedisChatMemory;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {
    @Autowired
    private ChatClient mcpChatClient;
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private RedisChatMemory redisChatMemory;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @PostMapping(value = "/{chatId}",produces = "text/html;charset=utf-8")
    public Flux<String> chat(@PathVariable String chatId,
                             @RequestParam("question") String question,
                             @RequestParam(value = "files",required = false) List<MultipartFile> files
    ){

        if(files == null || files.isEmpty()){
            return textChat(chatId,question);
        }
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

    @GetMapping("/history/{chatId}")
    public String getChatHistory(@PathVariable String chatId){
        return redisChatMemory.get(chatId,100).toString();
    }



}
