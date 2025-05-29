package com.aiocloud.llm.ollama;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class OllamaTest {

    public static void main(String[] args) {

        ChatLanguageModel model = OllamaChatModel
                .builder()
                .baseUrl("http://172.16.245.254:11434")
                .modelName("qwq:latest")
                .build();

        String answer = model.chat("用 Java 写一个冒泡排序");
        System.out.println(answer);
    }
}
