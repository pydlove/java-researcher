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

        // 49 base + 200 bussiness
        // top1Accuracy: 69.72%, top5Accuracy: 76.30%, top1FinalAccuracy: 52.09%, top5FinalAccuracy: 57.00%
        calculateCorrectRate(4114,4484,	4976,3090,4414,12968);

        // 49 base + 1000 bussiness
        // top1Accuracy: 72.23%, top5Accuracy: 82.03%, top1FinalAccuracy: 67.57%, top5FinalAccuracy: 76.74%
        calculateCorrectRate(4114,4484,	7679,2934,1126,12968);

        // 49 base + 1500 bussiness
        // top1Accuracy: 72.91%, top5Accuracy: 83.73%, top1FinalAccuracy: 70.00%, top5FinalAccuracy: 80.38%
        calculateCorrectRate(4114,4484,	8102,2726,698,12968);

        // 49 base + 200 + 1000 bussiness（V2）
        // top1Accuracy: 70.55%, top5Accuracy: 80.90%, top1FinalAccuracy: 66.83%, top5FinalAccuracy: 76.63%
        calculateCorrectRate(4114,4484,	7549,3157,921,12968);

        // 49 base + 200 + 1000 bussiness（V3）
        // top1Accuracy: 69.69%, top5Accuracy: 80.11%, top1FinalAccuracy: 65.60%, top5FinalAccuracy: 75.42%
        calculateCorrectRate(4114,4484,	7335,3267,1023,12968);

        // 49 base + 200 + 2000 bussiness（V3）
        // top1Accuracy: 70.46%, top5Accuracy: 81.36%, top1FinalAccuracy: 66.50%, top5FinalAccuracy: 76.79%
        calculateCorrectRate(4114,4484,	7491,3070,981,12968);

        // 49 base + 200 + 3000 bussiness（V3）
        // top1Accuracy: 78.78%, top5Accuracy: 88.49%, top1FinalAccuracy: 77.64%, top5FinalAccuracy: 87.20%
        calculateCorrectRate(4114,4484,	9435,1979,254,12968);

        // 49 base + 200 + 4000 bussiness（V3）
        // top1Accuracy: 80.32%, top5Accuracy: 90.16%, top1FinalAccuracy: 79.70%, top5FinalAccuracy: 89.47%
        calculateCorrectRate(4114,4484,	9795,1704,134,12968);
    }

    public static void calculateCorrectRate(
            int essentialFieldCorrectCount,
            int essentialFieldTotal,
            int businessFieldTop1CorrectCount,
            int businessFieldErrorCount,
            int businessNoResultCount,
            int businessFieldTotal
    ) {

        int top1CorrectCount = essentialFieldCorrectCount + businessFieldTop1CorrectCount;

        int total = businessFieldTotal + essentialFieldTotal;
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
