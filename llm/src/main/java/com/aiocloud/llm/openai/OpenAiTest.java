package com.aiocloud.llm.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class OpenAiTest {

    public static void main(String[] args) {

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://api.chatanywhere.tech/v1")
                .apiKey("sk-5PX4ysrCp3eY0qpb1FJLBnx2bhtjCWL2R90E2bpXmvJA5XJx")
                .modelName("gpt-4o-mini")
                .build();

        String answer = model.chat("用 Java 写一个冒泡排序");
        System.out.println(answer);
    }
}
