package com.xcg.aitripassistant;

import com.xcg.aitripassistant.service.TripService;
import com.xcg.aitripassistant.utils.DocParser;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

@SpringBootTest
class AiTripAssistantApplicationTests {
    @Autowired
    private TripService tripService;

    @Autowired
    private ElasticsearchVectorStore vectorStore;


    @Test
    public void test(){
        tripService.list().stream().forEach(System.out::println);
    }
    @Test
    public void testSearch(){
        tripService.searchItemByPriceRange(37700,37800).stream().forEach(System.out::println);
    }

    @Test
    public void testParser(){
        Document doc = DocParser.parse("项目实战-套餐管理.md");
        System.out.println("Content: " + Objects.requireNonNull(doc.getText()).substring(0, 100) + "...");
        System.out.println("Metadata: " + doc.getMetadata());
    }
    @Test
    public void testAdd(){
        Document doc = DocParser.parse("环球漫游指南.docx");
        vectorStore.add(List.of(doc));
    }
}
