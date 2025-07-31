package com.aiocloud.test.id;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class SnowflakeConcurrentTest {
    private final SnowflakeIdGenerator idGenerator;
    private final int threadCount;
    private final int requestsPerThread;
    private final Set<Long> idSet = ConcurrentHashMap.newKeySet();
    private final AtomicLong collisionCount = new AtomicLong(0);
    private final AtomicLong disorderCount = new AtomicLong(0);

    public SnowflakeConcurrentTest(SnowflakeIdGenerator idGenerator,
                                   int threadCount,
                                   int requestsPerThread) {
        this.idGenerator = idGenerator;
        this.threadCount = threadCount;
        this.requestsPerThread = requestsPerThread;
    }

    public void runConcurrentTest() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    long lastId = 0;
                    for (int j = 0; j < requestsPerThread; j++) {
                        long id = idGenerator.nextId();

                        // 检查唯一性
                        if (!idSet.add(id)) {
                            collisionCount.incrementAndGet();
                        }

                        // 检查有序性（单线程内）
                        if (id <= lastId) {
                            disorderCount.incrementAndGet();
                        }
                        lastId = id;
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long duration = System.currentTimeMillis() - startTime;
        printTestResult(duration);
    }

    private void printTestResult(long duration) {
        System.out.println("========== 测试结果 ==========");
        System.out.println("线程数: " + threadCount);
        System.out.println("每线程请求数: " + requestsPerThread);
        System.out.println("总生成ID数: " + idSet.size());
        System.out.println("重复ID数: " + collisionCount.get());
        System.out.println("乱序ID数: " + disorderCount.get());
        System.out.println("总耗时(ms): " + duration);
        System.out.println("QPS: " + (threadCount * requestsPerThread) / (duration / 1000.0));
    }

    public void testClockBackwards(long targetTime) throws Exception {

        // 保存原始时间获取方法
        final Supplier<Long> originalTimeGetter = idGenerator.getTimeGetter();

        try {

            // 模拟时钟回拨
            idGenerator.setTimeGetter(() -> {

                long current = originalTimeGetter.get();
                // 达到某个时间点时回拨
                if (current > targetTime) {
                    // 回拨1秒
                    return current - 10;
                }

                return current;
            });

            // 运行并发测试
            runConcurrentTest();
        } finally {

            // 恢复原始时间获取方法
            idGenerator.setTimeGetter(originalTimeGetter);
        }
    }
}