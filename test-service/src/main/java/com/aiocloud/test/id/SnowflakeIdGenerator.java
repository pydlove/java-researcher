package com.aiocloud.test.id;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * 分布式ID生成器（雪花算法改进版）
 */
@Slf4j
public class SnowflakeIdGenerator {

    // 起始时间戳（2023-01-01）
    private final static long START_TIMESTAMP = 1672531200000L;

    // 各部分位数
    private final static long SEQUENCE_BITS = 12L;
    private final static long WORKER_ID_BITS = 10L;

    // 最大值
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 移位
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private final static long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private Supplier<Long> timeGetter = System::currentTimeMillis;

    public long timeGen() {
        return timeGetter.get();
    }

    /**
     * 允许动态修改时间获取逻辑（测试用）
     *
     * @since 1.0.0
     *
     * @param: timeGetter
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-07-07 11:53 
     */
    public void setTimeGetter(Supplier<Long> timeGetter) {
        this.timeGetter = timeGetter;
    }

    /**
     * 获取当前时间获取器（测试用）
     *
     * @since 1.0.0
     *
     * @return: java.util.function.Supplier<java.lang.Long>
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-07-07 11:53 
     */
    public Supplier<Long> getTimeGetter() {
        return timeGetter;
    }

    /**
     * 时钟回拨安全锁
     *
     * @since 1.0.0
     *
     * @return:
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-07-07 11:54 
     */
    private final Object lock = new Object();

    /**
     * 构造函数
     * @param workerId 工作节点ID (0-1023)
     */
    public SnowflakeIdGenerator(long workerId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("Worker ID must be between 0 and %d", MAX_WORKER_ID));
        }
        this.workerId = workerId;
    }

    /**
     * 生成下一个ID
     */
    public long nextId() {
        synchronized (lock) {
            return doGenerateId();
        }
    }

    private long doGenerateId() {

        long timestamp = timeGen();
        log.info("timestamp: {}, lastTimestamp: {}, dif: {}", timestamp, lastTimestamp, timestamp - lastTimestamp);

        // 时钟回拨处理
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    // 等待时钟追平
                    lock.wait(offset << 1);
                    timestamp = timeGen();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Clock moved backwards, wait interrupted");
                }
            } else {
                // 超过5ms直接报错（应触发降级策略）
                throw new RuntimeException(
                        String.format("Clock moved backwards. Refusing to generate ID for %d milliseconds", offset));
            }
        }

        // 同一毫秒内序列号递增
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 当前毫秒序列号用尽，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 组合各部分生成ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 等待到下一毫秒
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 解析ID
     */
    public static IdInfo parseId(long id) {
        long timestamp = (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
        long workerId = (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
        long sequence = id & MAX_SEQUENCE;
        return new IdInfo(timestamp, workerId, sequence);
    }

    /**
     * ID信息对象
     */
    public static class IdInfo {
        public final long timestamp;
        public final long workerId;
        public final long sequence;

        public IdInfo(long timestamp, long workerId, long sequence) {
            this.timestamp = timestamp;
            this.workerId = workerId;
            this.sequence = sequence;
        }

        @Override
        public String toString() {
            return String.format("Timestamp: %d, WorkerID: %d, Sequence: %d",
                    timestamp, workerId, sequence);
        }
    }
}