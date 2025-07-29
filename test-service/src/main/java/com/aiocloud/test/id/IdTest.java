package com.aiocloud.test.id;

import java.util.ArrayList;
import java.util.List;

public class IdTest {

    public static void main(String[] args) throws Exception {

//        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1);
//        SnowflakeConcurrentTest tester = new SnowflakeConcurrentTest(generator, 32, 10000);
//        tester.runConcurrentTest();

        // 开始回拨时间的点
        long targetTime = System.currentTimeMillis() + 1000;

        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1);
        SnowflakeConcurrentTest tester = new SnowflakeConcurrentTest(generator, 1, 1000000);
        tester.testClockBackwards(targetTime);
    }
}
