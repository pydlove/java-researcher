package com.aiocloud.test.excel.main;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @description: CorrectRateHandler.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-07-29 14:19 
 */
@Slf4j
public class CorrectRateHandler {

    public static void main(String[] args) {

        calculateCorrectRate(4114,4484,4976, 12968);
    }

    public static String calculateCorrectRate(
            int essentialFieldCorrect,
            int essentialFieldTotal,
            int businessFieldCorrect,
            int businessFieldTotal
    ) {

        int correct = essentialFieldCorrect + businessFieldCorrect;
        int total = essentialFieldTotal + businessFieldTotal;

        BigDecimal accuracy = new BigDecimal(correct * 100.0 / total)
                .setScale(2, RoundingMode.HALF_UP);
        
        log.info("total: {}, correct: {}, accuracy: {}%", total, correct, accuracy);

        return accuracy.toString();
    }
}
