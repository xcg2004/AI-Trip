package com.xcg.aitripassistant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RagService {
    //  Elasticsearch vector store
    private final ElasticsearchVectorStore vectorStore;
    //  Token text splitter
    private final TokenTextSplitter tokenTextSplitter;

    /**
     * 把pdf文件写入到Elasticsearch vector store
     * @param path 文件路径
     */
    public void ingestPDF(String path) {
        // Spring AI utility class to read a PDF file page by page
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(path,
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                        .withPagesPerDocument(1)
                        .build());
        List<Document> docbatch = pdfReader.read();
        List<List<Document>> docs = new ArrayList<>();
        if (docbatch.size() <= 10) {
            docs.add(docbatch);
        } else {
            // size = 11
            for (int i = 0; i < docbatch.size() / 10; i++) {
                docs.add(docbatch.subList(i * 10, i * 10 + 10));
            }
            docs.add(docbatch.subList(docbatch.size() / 10 * 10, docbatch.size()));
        }
//       docId: ba11f25f-454c-44ce-b5d2-164e95056aa6

        docs.forEach(vectorStore::add);

    /*public String query(String question){
        List<Document> documents = vectorStore.doSimilaritySearch(SearchRequest.builder()
                .query(question)
                .similarityThreshold(0.6)
                .topK(3)
                .build());
        String find = documents.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));

    }*/
    }
}
