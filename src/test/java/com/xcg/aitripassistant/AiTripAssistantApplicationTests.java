package com.xcg.aitripassistant;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.xcg.aitripassistant.service.TripService;
import com.xcg.aitripassistant.utils.DocParser;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@SpringBootTest
class AiTripAssistantApplicationTests {
    @Autowired
    private TripService tripService;

    @Autowired
    private ElasticsearchVectorStore vectorStore;
    @Autowired
    private ElasticsearchClient elasticsearchClient;

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
    @Test
    public void testESearch() throws IOException {
        SearchRequest searchRequest = SearchRequest.of(
                b-> b.query(
                        q->q.match(
                                m->m.field("text")
                                        .query("上海")))

        );
        elasticsearchClient.search(searchRequest, Document.class);

        SearchRequest sq = SearchRequest.of(
                b->b.query(q->q.bool
                        (bq->bq
                        .should(m->m.match(qu->qu.field("title").query("美食")))
                        .should(m->m.match(qu->qu.field("category").query("美食")))
                        )
                )
        );
        /*SearchRequest searchRequest = SearchRequest.of(
                b -> b.query(q -> q.bool(bl -> bl
                        .should(s -> s.match(m -> m.field("title").query("美食")))
                        .should(s -> s.match(m -> m.field("category").query("酒店")))
                        .minimumShouldMatch(1) // 至少满足一个 should 条件
                ))
        );*/

        elasticsearchClient.search(searchRequest, Document.class);

        elasticsearchClient.search(sq, Document.class);



    }


}
