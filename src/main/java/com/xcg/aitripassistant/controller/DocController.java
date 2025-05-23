package com.xcg.aitripassistant.controller;

import com.xcg.aitripassistant.utils.DocParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/pdf")
@Slf4j
public class DocController {

    @Autowired
    private ChatClient docChatClient;

    /*@Autowired
    private RagService ragService;*/

    @Autowired
    private ElasticsearchVectorStore vectorStore;

    /**
     * 读取类路径下的文件并写入Elastic Search
     * @param path
     */
    @PostMapping("/ingest/{path}")
    public void ingestDoc(@PathVariable String path) {
        log.info("读取文档并向量化写入Elasticsearch:{}", path);
//        ragService.ingestPDF(path);
        Document doc = DocParser.parse(path);

        vectorStore.doAdd(List.of(doc));
    }

    @GetMapping("/query")
    public Flux<String> query(@RequestParam("query") String query,
                              @RequestParam("chatId") String chatId) {
        log.info("查询向量数据库相似文档:{}", query);
        return docChatClient.prompt()
                .user(query)
                .advisors(a->a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,chatId))
                .stream()
                .content();

    }
}
