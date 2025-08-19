package com.amst.ai.config;


import com.amst.ai.contents.SystemContents;
import com.amst.ai.tools.CourseTools;
import lombok.Data;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class CommonConfiguration {
    private final JdbcChatMemoryRepository chatMemoryRepository;

    @Value("${spring.ai.openai.chat.options.multi-modal}")
    private String multiModal;
    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory) {

        return ChatClient.builder(model)
                .defaultOptions(ChatOptions.builder()
                        .model(multiModal)
                        .build())
                .defaultSystem("你是一个在电影，游戏，音乐等等各种娱乐方面非常了解的时尚达人")  //默认提示词
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),//默认日志
                        MessageChatMemoryAdvisor.builder(chatMemory).build() //会话记忆
                )
                .build();
    }

    @Bean
    public ChatClient gameChatClient(OpenAiChatModel model, ChatMemory chatMemory){
        return ChatClient.builder(model)
                .defaultSystem(SystemContents.GAME_SYSTEM_PROMPT)  //默认提示词
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),//默认日志
                        MessageChatMemoryAdvisor.builder(chatMemory).build() //会话记忆
                )
                .build();
    }


    @Bean
    public ChatClient courseChatClient(OpenAiChatModel model, ChatMemory chatMemory, CourseTools courseTools){
        return ChatClient
                .builder(model)
                .defaultSystem(SystemContents.SERVICE_SYSTEM_PROMPT)  //默认提示词
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),//默认日志
                        MessageChatMemoryAdvisor.builder(chatMemory).build() //会话记忆
                )
                .defaultTools(courseTools)
                .build();
    }


    @Bean
    public ChatClient pdfChatClient(OpenAiChatModel model, ChatMemory chatMemory, VectorStore vectorStore){
        return ChatClient.builder(model)
                .defaultSystem("根据上下文回答问题，如果上下文没有的问题请不要随意编造。")  //默认提示词
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),//默认日志
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                        ,//会话记忆
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(1)
                                        .similarityThreshold(0.6f)
                                        .build()
                                )
                                .build()
                        )
                .build();


    }




}
