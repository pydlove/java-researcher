package com.aiocloud.test.license.storage;

import org.apache.kafka.clients.Metadata;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MetadataStorageService {
    @KafkaListener(topics = "metadata_topic")
    public void storeMetadata(String message) {
//        try {
//            Metadata metadata = parseMetadata(message);
//
//            // 存储元数据
//            boolean success = saveToDatabase(metadata);
//
//            if (!success) {
//                // 存储失败，需要通知授权服务回滚
//                // 可以通过另一个Kafka topic或直接调用服务
//                notifyRollback(metadata.getId());
//            }
//        } catch (Exception e) {
//            // 处理异常
//            notifyRollback(getMetadataIdFromMessage(message));
//        }
    }
}
