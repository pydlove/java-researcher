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


        System.out.println("-----------------------------------------V4------------------------------------------");

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

        // 49 base + 400 bussiness
        // top1Accuracy: 71.55%, top5Accuracy: 81.97%, top1FinalAccuracy: 68.35%, top5FinalAccuracy: 78.30%
        calculateCorrectRate(
                4114, 4484,
                6604, 3003, 781,
                1200, 11753
        );

        // 49 base + 400 bussiness
        // top1Accuracy: 73.46%, top5Accuracy: 83.72%, top1FinalAccuracy: 70.17%, top5FinalAccuracy: 79.98%
        calculateCorrectRate(
                4114, 4484,
                6722, 2711, 780,
                1400, 11553
        );

        // 49 base + 400 bussiness
        // top1Accuracy: 74.91%, top5Accuracy: 85.55%, top1FinalAccuracy: 72.33%, top5FinalAccuracy: 82.61%
        calculateCorrectRate(
                4114, 4484,
                6898, 2432, 601,
                1600, 11353
        );

        // 49 base + 400 bussiness
        // top1Accuracy: 74.40%, top5Accuracy: 85.56%, top1FinalAccuracy: 72.41%, top5FinalAccuracy: 83.28%
        calculateCorrectRate(
                4114, 4484,
                6712, 2450, 466,
                1800, 11153
        );

        System.out.println("-----------------------------------------V5------------------------------------------");

        // 49 base + 200 bussiness
        // top1Accuracy: 66.63%, top5Accuracy: 75.04%, top1FinalAccuracy: 52.87%, top5FinalAccuracy: 59.55%
        calculateCorrectRate(
                4114, 4484,
                4905, 3454, 3599,
                200, 12752
        );

        // 49 base + 200 bussiness (JaccardSimilarity 0.7)
        // top1Accuracy: 69.31%, top5Accuracy: 78.99%, top1FinalAccuracy: 60.78%, top5FinalAccuracy: 69.28%
        calculateCorrectRate(
                4114, 4484,
                6084, 3212, 2145,
                400, 12552
        );

        // 49 base + 200 bussiness (JaccardSimilarity 0.7)
        // top1Accuracy: 68.95%, top5Accuracy: 79.99%, top1FinalAccuracy: 63.25%, top5FinalAccuracy: 73.38%
        calculateCorrectRate(
                4114, 4484,
                6314, 3201, 1441,
                600, 12352
        );

        // 49 base + 200 bussiness (JaccardSimilarity 0.7)
        // top1Accuracy: 68.79%, top5Accuracy: 80.15%, top1FinalAccuracy: 64.34%, top5FinalAccuracy: 74.96%
        calculateCorrectRate(
                4114, 4484,
                6304, 3237, 1129,
                800, 12152
        );

        System.out.println("-----------------------------------------V6------------------------------------------");

        // 49 base + 200 bussiness
        // top1Accuracy: 66.63%, top5Accuracy: 75.04%, top1FinalAccuracy: 52.87%, top5FinalAccuracy: 59.55%
        calculateCorrectRate(
                4114, 4484,
                4905, 3454, 3599,
                200, 12752
        );

        // 49 base + 200 bussiness (LevenshteinDistance 0.7)
        // top1Accuracy: 66.25%, top5Accuracy: 77.11%, top1FinalAccuracy: 58.08%, top5FinalAccuracy: 67.60%
        calculateCorrectRate(
                4114, 4484,
                5613, 3499, 2151,
                400, 12552
        );

        System.out.println("-----------------------------------------V7------------------------------------------");

        // 49 base + 200 bussiness (LevenshteinDistance 0.8)
        // top1Accuracy: 68.09%, top5Accuracy: 78.05%, top1FinalAccuracy: 59.90%, top5FinalAccuracy: 68.67%
        calculateCorrectRate(
                4114, 4484,
                5931, 3368, 2095,
                400, 12552
        );

        System.out.println("-----------------------------------------V8------------------------------------------");

        // 49 base + 200 bussiness (LevenshteinDistance 0.9)
        // top1Accuracy: 66.71%, top5Accuracy: 76.92%, top1FinalAccuracy: 58.89%, top5FinalAccuracy: 67.90%
        calculateCorrectRate(
                4114, 4484,
                5754, 3553, 2044,
                400, 12552
        );

        System.out.println("-----------------------------------------V9------------------------------------------");

        // 49 base + 200 bussiness (LevenshteinDistance 0.9)
        // top1Accuracy: 69.42%, top5Accuracy: 80.09%, top1FinalAccuracy: 64.14%, top5FinalAccuracy: 74.00%
        calculateCorrectRate(
                4114, 4484,
                6469, 3207, 1327,
                600, 12352
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
