package com.xcg.aitripassistant.utils;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DocParser {

    // 统一解析入口
    public static Document parse(String classNamePath) {
        try {
            Resource resource = new ClassPathResource("documents/"+classNamePath);
            Path filePath = Paths.get(resource.getURI());
            String fileType = getFileType(filePath.getFileName().toString());

            String content = switch (fileType) {
                case "DOCX" -> parseWord(filePath);
                case "PDF" -> parsePdf(resource);

                case "MD" -> parseMarkdown(filePath);
                default -> throw new IllegalArgumentException("Unsupported file type: " + fileType);
            };

            return buildDocument(filePath, content, fileType);
        } catch (IOException e) {
            throw new RuntimeException("Error processing document: " + classNamePath, e);
        }
    }

    // Word解析（.docx）
    private static String parseWord(Path filePath) throws IOException {
        try (var fis = Files.newInputStream(filePath);
             var doc = new org.apache.poi.xwpf.usermodel.XWPFDocument(fis)) {
            return new org.apache.poi.xwpf.extractor.XWPFWordExtractor(doc).getText();
        }
    }

//     PDF解析
    private static String parsePdf(Resource resource) throws IOException {
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                        .withPagesPerDocument(1)
                        .build());
        List<Document> documents = pdfReader.read();
        return documents.stream().map(Document::getText).collect(Collectors.joining(","));
    }

    // Markdown解析
    private static String parseMarkdown(Path filePath) throws IOException {
        String markdown = Files.readString(filePath);
        var options = new com.vladsch.flexmark.util.data.MutableDataSet();
        var parser = com.vladsch.flexmark.parser.Parser.builder(options).build();
        var renderer = com.vladsch.flexmark.html.HtmlRenderer.builder(options).build();
        return renderer.render(parser.parse(markdown)); // 返回HTML格式内容
    }

    // 构建Document对象
    private static Document buildDocument(Path filePath, String text, String fileType) {
        return Document.builder()
                .text(text)
                .metadata(Map.of(
                        "file_name", filePath.getFileName().toString(),
                        "file_type", fileType,
                        "source", "classpath",
                        "location", filePath.toString()
                ))
                .build();
    }

    // 获取文件类型（小写转大写）
    private static String getFileType(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toUpperCase();
    }

    // 测试用例
    public static void main(String[] args) {
        // 示例：解析src/main/resources下的test.docx
        Document doc = DocParser.parse("环球漫游指南.docx");
        System.out.println("Content: " + Objects.requireNonNull(doc.getText()).substring(0, 100) + "...");
        System.out.println("Metadata: " + doc.getMetadata());
    }
}