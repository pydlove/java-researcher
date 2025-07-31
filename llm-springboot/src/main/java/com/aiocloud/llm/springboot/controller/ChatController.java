package com.aiocloud.llm.springboot.controller;

import com.aiocloud.llm.springboot.config.AssistantConfig;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 *
 * @description: ChatController.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-05-28 17:07
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {

    private final QwenChatModel qwenChatModel;
    private final QwenStreamingChatModel qwenStreamingChatModel;
    private final AssistantConfig.Assistant assistant;
    private final AssistantConfig.AssistantUnique assistantUnique;

    @GetMapping("/chat")
    public String chat(
            @RequestParam(value = "question") String question
    ) {

        return qwenChatModel.chat(question);
    }

    @GetMapping(value = "/stream", produces = "text/stream;charset=UTF-8")
    public Flux<String> stream(
            @RequestParam(value = "question") String question
    ) {

        Flux<String> flux = Flux.create(fluxSink -> {
            qwenStreamingChatModel.chat(question, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String partialResponse) {
                    fluxSink.next(partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse chatResponse) {
                    fluxSink.next("【TOKEN_USAGE】" + chatResponse.tokenUsage().totalTokenCount());
                    fluxSink.complete();
                }

                @Override
                public void onError(Throwable throwable) {
                    fluxSink.error(throwable);
                }
            });
        });

        return flux;
    }


    /**
     * 记住之前的问题
     *
     * @since 1.0.0
     *
     * @param: question
     * @return: java.lang.String
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-05-29 14:05 
     */
    @GetMapping("/memory-chat")
    public String memoryChat(
            @RequestParam(value = "question") String question
    ) {

        return assistant.chat(question);
    }

    /**
     * 记住之前的问题
     *
     * @since 1.0.0
     *
     * @param: question
     * @return: reactor.core.publisher.Flux<java.lang.String>
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-05-29 14:02 
     */
    @GetMapping(value = "/memory-stream", produces = "text/stream;charset=UTF-8")
    public Flux<String> memoryStream(
            @RequestParam(value = "question") String question
    ) {

        TokenStream stream = assistant.stream(question);

        return Flux.create(sink -> {
            stream.onPartialResponse(sink::next)
                    .onCompleteResponse(r -> sink.complete())
                    .onError(sink::error)
                    .start();
        });
    }

    /**
     * 按会话id区分记忆
     *
     * @since 1.0.0
     *
     * @param: question
     * @param: sessionId
     * @return: java.lang.String
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-05-29 14:29 
     */
    @GetMapping("/memory-id/chat")
    public String chatByMemoryId(
            @RequestParam(value = "question") String question,
            @RequestParam(value = "sessionId") Integer sessionId
    ) {

        return assistantUnique.chat(sessionId, question);
    }

    @GetMapping(value = "/memory-id/strean", produces = "text/stream;charset=UTF-8")
    public Flux<String> streamChatByMemoryId(
            @RequestParam(value = "question") String question,
            @RequestParam(value = "sessionId") Integer sessionId
    ) {

        TokenStream stream = assistantUnique.stream(sessionId, question);

        return Flux.create(sink -> {
            stream.onPartialResponse(sink::next)
                    .onCompleteResponse(r -> sink.complete())
                    .onError(sink::error)
                    .start();
        });
    }
}
