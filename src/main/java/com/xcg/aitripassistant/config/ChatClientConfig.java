package com.xcg.aitripassistant.config;

import com.xcg.aitripassistant.constant.DefaultConstant;
import com.xcg.aitripassistant.service.TripService;
import com.xcg.aitripassistant.utils.AlibabaOpenAiChatModel;
import com.xcg.aitripassistant.utils.RedisChatMemory;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
public class ChatClientConfig {

   /* @Autowired
    private TripService tripService;*/

    @Autowired
    private ElasticsearchVectorStore vectorStore;
    @Bean
    public ChatMemory chatMemory(){
        return new RedisChatMemory();
    }

    /**
     * function calling
     * @param openAiChatModel
     * @param chatMemory
     * @return
     */
    @Bean
    public ChatClient chatClient(AlibabaOpenAiChatModel openAiChatModel,
                                 ChatMemory chatMemory,
                                ToolCallbackProvider toolCallbackProvider) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(DefaultConstant.DEFAULT_CHAT_SYSTEM_PROMPT)
                .defaultOptions(ChatOptions.builder().model("qwen-omni-turbo").build())
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .defaultTools(toolCallbackProvider)
                .build();
    }

    /**
     * RAG 本地知识库(可用于公司内部开发手册快速提问)
     * @param openAiChatModel
     * @param chatMemory
     * @return
     */
    @Bean
    public ChatClient docChatClient(OpenAiChatModel openAiChatModel, ChatMemory chatMemory){
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(DefaultConstant.DEFAULT_RAG_SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder()
                                .similarityThreshold(0.6)
                                .topK(3)
                                .build())
                )
                .build();
    }

    /**
     * 智能旅行助手+MCP工具
     * @param openAiChatModel
     * @param chatMemory
     * @param toolCallbackProvider
     * @return
     */
    @Bean
    public ChatClient mcpChatClient(
            AlibabaOpenAiChatModel openAiChatModel,
            ChatMemory chatMemory,
            ToolCallbackProvider toolCallbackProvider) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(DefaultConstant.DEFAULT_SYSTEM_PROMPT)

                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder()
                                .similarityThreshold(0.6)
                                .topK(3)
                                .build())
                )
                .defaultTools(toolCallbackProvider)
                .build();
    }
    /**
     * 兼容阿里云百炼接口规范
     * @param commonProperties
     * @param chatProperties
     * @param restClientBuilderProvider
     * @param webClientBuilderProvider
     * @param toolCallingManager
     * @param retryTemplate
     * @param responseErrorHandler
     * @param observationRegistry
     * @param observationConvention
     * @return
     */
    @Bean
    public AlibabaOpenAiChatModel alibabaOpenAiChatModel(OpenAiConnectionProperties commonProperties, OpenAiChatProperties chatProperties, ObjectProvider<RestClient.Builder> restClientBuilderProvider, ObjectProvider<WebClient.Builder> webClientBuilderProvider, ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ResponseErrorHandler responseErrorHandler, ObjectProvider<ObservationRegistry> observationRegistry, ObjectProvider<ChatModelObservationConvention> observationConvention) {
        String baseUrl = StringUtils.hasText(chatProperties.getBaseUrl()) ? chatProperties.getBaseUrl() : commonProperties.getBaseUrl();
        String apiKey = StringUtils.hasText(chatProperties.getApiKey()) ? chatProperties.getApiKey() : commonProperties.getApiKey();
        String projectId = StringUtils.hasText(chatProperties.getProjectId()) ? chatProperties.getProjectId() : commonProperties.getProjectId();
        String organizationId = StringUtils.hasText(chatProperties.getOrganizationId()) ? chatProperties.getOrganizationId() : commonProperties.getOrganizationId();
        Map<String, List<String>> connectionHeaders = new HashMap<>();
        if (StringUtils.hasText(projectId)) {
            connectionHeaders.put("OpenAI-Project", List.of(projectId));
        }

        if (StringUtils.hasText(organizationId)) {
            connectionHeaders.put("OpenAI-Organization", List.of(organizationId));
        }
        RestClient.Builder restClientBuilder = restClientBuilderProvider.getIfAvailable(RestClient::builder);
        WebClient.Builder webClientBuilder = webClientBuilderProvider.getIfAvailable(WebClient::builder);
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(baseUrl).apiKey(new SimpleApiKey(apiKey)).headers(CollectionUtils.toMultiValueMap(connectionHeaders)).completionsPath(chatProperties.getCompletionsPath()).embeddingsPath("/v1/embeddings").restClientBuilder(restClientBuilder).webClientBuilder(webClientBuilder).responseErrorHandler(responseErrorHandler).build();
        AlibabaOpenAiChatModel chatModel = AlibabaOpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(chatProperties.getOptions()).toolCallingManager(toolCallingManager).retryTemplate(retryTemplate).observationRegistry((ObservationRegistry)observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)).build();
        Objects.requireNonNull(chatModel);
        observationConvention.ifAvailable(chatModel::setObservationConvention);
        return chatModel;
    }
}
