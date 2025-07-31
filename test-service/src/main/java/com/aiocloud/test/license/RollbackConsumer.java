package com.aiocloud.test.license;

import com.aiocloud.test.license.storage.RollbackNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RollbackConsumer {

    private final LicenseService licenseService;

    @KafkaListener(topics = "metadata_rollback", groupId = "rollback-group")
    public void consumeRollbackMessage(String message) {
        try {
            // ollbackNotifier.RollbackMessage rollbackMsg = parseRollbackMessage(message);

            // 执行回滚操作
            licenseService.rollbackLicense(1);

            // log.info("Successfully rolled back license for metadata {}", rollbackMsg.getMetadataId());
        } catch (Exception e) {
            log.error("Failed to process rollback message: {}", message, e);
            // 可以考虑将失败消息放入死信队列或重试队列
        }
    }
}