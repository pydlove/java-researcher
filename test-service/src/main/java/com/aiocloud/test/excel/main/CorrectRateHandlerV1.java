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
public class CorrectRateHandlerV1 {

    public static void main(String[] args) {

        // 49 base + 200 bussiness
        // top1Accuracy: 69.03%, top5Accuracy: 75.49%, top1FinalAccuracy: 52.89%, top5FinalAccuracy: 57.84%
        calculateCorrectRate(
                4114, 4484,
                4909, 3275, 4076,
                200, 12753
        );

        // 49 base + 400 bussiness
        // top1Accuracy: 69.37%, top5Accuracy: 77.75%, top1FinalAccuracy: 59.61%, top5FinalAccuracy: 66.81%
        calculateCorrectRate(
                4114, 4484,
                5880, 3334, 2454,
                400, 12553
        );

        // 49 base + 400 bussiness
        // top1Accuracy: 70.48%, top5Accuracy: 79.36%, top1FinalAccuracy: 63.89%, top5FinalAccuracy: 71.94%
        calculateCorrectRate(
                4114, 4484,
                6426, 3262, 1631,
                600, 12353
        );

        // 49 base + 400 bussiness
        // top1Accuracy: 71.19%, top5Accuracy: 80.83%, top1FinalAccuracy: 66.03%, top5FinalAccuracy: 74.97%
        calculateCorrectRate(
                4114, 4484,
                6600, 3100, 1264,
                800, 12153
        );

        // 49 base + 400 bussiness
        // top1Accuracy: 71.78%, top5Accuracy: 81.82%, top1FinalAccuracy: 67.64%, top5FinalAccuracy: 77.09%
        calculateCorrectRate(
                4114, 4484,
                6680, 2987, 1007,
                1000, 11953
        );

    }

    public static void calculateCorrectRate(
            int essentialFieldCorrectCount,
            int essentialFieldTotal,
            int businessFieldTop1CorrectCount,
            int businessFieldErrorCount,
            int businessNoResultCount,
            int trainCount,
            int businessFieldTotal
    ) {

        int top1CorrectCount = essentialFieldCorrectCount + businessFieldTop1CorrectCount + trainCount;

        int total = businessFieldTotal + essentialFieldTotal + trainCount;
        int businessFieldHasResultTotal = total - businessNoResultCount;

        int top5CorrectCount = businessFieldHasResultTotal - businessFieldErrorCount;

        BigDecimal top1Accuracy = new BigDecimal(top1CorrectCount * 100.0 / businessFieldHasResultTotal)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal top5Accuracy = new BigDecimal(top5CorrectCount * 100.0 / businessFieldHasResultTotal)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal top1FinalAccuracy = new BigDecimal(top1CorrectCount * 100.0 / total)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal top5FinalAccuracy = new BigDecimal(top5CorrectCount * 100.0 / total)
                .setScale(2, RoundingMode.HALF_UP);

        log.info("top1Accuracy: {}%, top5Accuracy: {}%, top1FinalAccuracy: {}%, top5FinalAccuracy: {}%", top1Accuracy, top5Accuracy, top1FinalAccuracy, top5FinalAccuracy);
    }
}
