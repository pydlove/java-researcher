package com.aiocloud.llm.qwen;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class QwenTest {

    public static void main(String[] args) {
        ChatLanguageModel model = QwenChatModel
                .builder()
                .apiKey("sk-37d80645c96d4a8184b7b52bb3bb0940")
                .modelName("qwen-plus")
                .build();

        String answer = model.chat("用 Java 写一个冒泡排序");
        System.out.println(answer);
    }
}
