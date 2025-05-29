package com.aiocloud.llm;

import dev.langchain4j.model.language.LanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiLanguageModel;
import dev.langchain4j.model.output.Response;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class OpenAiChatModelTest {

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
