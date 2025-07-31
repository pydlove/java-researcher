package com.aiocloud.test.license.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RollbackNotifier {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String ROLLBACK_TOPIC = "metadata_rollback";

    public void notifyRollback(long metadataId) {
        RollbackMessage message = new RollbackMessage(metadataId, System.currentTimeMillis());
//        String jsonMessage = convertToJson(message);
//
//        kafkaTemplate.send(ROLLBACK_TOPIC, jsonMessage)
//                .addCallback(
//                        success -> log.info("Rollback notification sent for metadata {}", metadataId),
//                        failure -> log.error("Failed to send rollback notification", failure)
//                );
    }

    @Data
    @AllArgsConstructor
    private static class RollbackMessage {
        private long metadataId;
        private long timestamp;
    }
}
