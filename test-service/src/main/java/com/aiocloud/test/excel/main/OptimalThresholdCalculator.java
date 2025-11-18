package com.aiocloud.test.excel.main;

import java.util.List;
import java.util.stream.IntStream;

public class OptimalThresholdCalculator {

    /**
     * 新推荐信息: 1851/2598, 71.25 %
     * @param scores
     * @return
     */

    public static double calculateThresholdV1(List<Double> scores, double percentile) {
        if (scores.isEmpty()) return 68.0;

        double mean = scores.stream().mapToDouble(d -> d).average().orElse(0.0);
        double std = Math.sqrt(scores.stream()
                .mapToDouble(d -> Math.pow(d - mean, 2))
                .average().orElse(0.0));

        return mean + percentile * std; // 可调整系数
    }

    /**
     * 新推荐信息: 1435/1947, 73.70 %
     * @param confidenceScores
     * @param percentile
     * @return
     */
    public static double calculateThreshold(List<Double> confidenceScores, double percentile) {
        if (confidenceScores == null || confidenceScores.isEmpty()) {
            return 68.0; // 默认回退值
        }

        // 排序置信度分数
        List<Double> sortedScores = confidenceScores.stream()
                .sorted()
                .toList();

        // 计算百分位位置
        int index = (int) Math.ceil(percentile / 100.0 * sortedScores.size()) - 1;
        index = Math.max(0, Math.min(index, sortedScores.size() - 1));

        return sortedScores.get(index);
    }
}