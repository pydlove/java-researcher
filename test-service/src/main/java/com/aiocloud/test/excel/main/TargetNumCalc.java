package com.aiocloud.test.excel.main;

/**
 * 抽样数量挑选算法
 * @description: TargetNumCalc.java 
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-07-22 14:36 
 */
public class TargetNumCalc {

    public static void main(String[] args) {
        System.out.println(calculateTrainingDataSize(500));   // 200
        System.out.println(calculateTrainingDataSize(1500));  // 150 → 200
        System.out.println(calculateTrainingDataSize(8000));  // 800
        System.out.println(calculateTrainingDataSize(12000)); // 1200 → 1000
    }

    public static int calculateTrainingDataSize(int totalMetadata) {
        int target = (int) Math.ceil(totalMetadata * 0.1); // 10%
        return Math.min(Math.max(target, 200), 1000);     // 限制在 [200, 1000]
    }

}
