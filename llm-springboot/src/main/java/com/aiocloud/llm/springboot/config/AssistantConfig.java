package com.aiocloud.llm.springboot.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @description: AssistantConfig.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-05-29 9:48 
 */
@Configuration
public class AssistantConfig {

    public interface Assistant {

        String chat(String message);

        TokenStream stream(String message);
    }

    /**
     * 实现对话记忆
     *
     * @since 1.0.0
     *
     * @param: chatLanguageModel
     * @param: streamingChatLanguageModel
     * @return: com.aiocloud.llm.springboot.config.AssistantConfig.Assistant
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-05-29 9:48 
     */
    @Bean
    public Assistant assistant(
            QwenChatModel qwenChatModel,
            QwenStreamingChatModel qwenStreamingChatModel,
            FunctionCallService functionCallService
    ) {

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        return AiServices.builder(Assistant.class)
                .tools(functionCallService)
                .chatLanguageModel(qwenChatModel)
                .streamingChatLanguageModel(qwenStreamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }


    public interface AssistantUnique {

        String chat(@MemoryId Integer memoryId, @UserMessage String message);

        TokenStream stream(@MemoryId Integer memoryId, @UserMessage String message);
    }

    /**
     * 按 memoryId 区分记忆
     *
     * @since 1.0.0
     *
     * @param: chatLanguageModel
     * @param: streamingChatLanguageModel
     * @return: com.aiocloud.llm.springboot.config.AssistantConfig.AssistantUnique
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-05-29 14:26 
     */
    @Bean
    public AssistantUnique assistantUnique(
            QwenChatModel qwenChatModel,
            QwenStreamingChatModel qwenStreamingChatModel,
            FunctionCallService functionCallService
    ) {

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .maxMessages(10)
                .id(memoryId)
                .build();

        return AiServices.builder(AssistantUnique.class)
                .tools(functionCallService)
                .chatLanguageModel(qwenChatModel)
                .streamingChatLanguageModel(qwenStreamingChatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }
}
