package com.aiocloud.llm.deepseek;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class DeepSeekTest {

    public static void main(String[] args) {

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com/v1")
                .apiKey("sk-ea44d4dab882413ebb870d892d0f83bd")
                .modelName("deepseek-chat")
                .build();

        String answer = model.chat("用 Java 写一个冒泡排序");
        System.out.println(answer);
    }
}
