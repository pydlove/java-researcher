package com.aiocloud.llm.springboot.config;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.List;

/**
 * 可以实现问题的持久化，这里以 mysql 为例，自带的有基于内存的 InMemoryChatMemoryStore
 *
 * @description: InMysqlChatMemoryStore.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-05-29 14:39
 */
public class InMysqlChatMemoryStore implements ChatMemoryStore {

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {

        // 从 mysql 查询数据
        return null;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        // 将问题更新到 mysql
    }

    @Override
    public void deleteMessages(Object memoryId) {
        // 从 mysql 删除数据
    }
}
