package com.aiocloud.test.excel.main;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CorrectRateHandler {

    public static void main(String[] args) {

//        calculateCorrectRate()
    }

    public static String calculateCorrectRate(
            int essentialFieldTotal,
            int essentialFieldCorrect,
            int businessFieldTotal,
            int businessFieldCorrect
    ) {

        int correct = essentialFieldTotal + businessFieldTotal;
        int total = essentialFieldTotal + businessFieldTotal;

        BigDecimal accuracy = new BigDecimal(correct * 100.0 / total)
                .setScale(2, RoundingMode.HALF_UP);

        return accuracy.toString();
    }
}
