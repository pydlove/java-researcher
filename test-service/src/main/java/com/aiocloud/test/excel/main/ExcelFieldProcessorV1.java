package com.aiocloud.test.excel.main;

import cn.hutool.core.bean.BeanUtil;
import com.aiocloud.test.excel.base.FieldInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于元数据抽取训练集
 * @description: ExcelFieldProcessorV1.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-07-22 14:29 
 */
@Slf4j
public class ExcelFieldProcessorV1 extends BaseProcessor {

    private static final double[] DUPLICATE_FACTORS = new double[]{
            0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0
    };

    private static final int BUCKET_COUNT = 10; // 10 个档位

    public static void main(String[] args) throws Exception {

        String essentialField = "是否基础字段";
        int maxDuplicates = 10;

        BaseProcessor baseProcessor = new ExcelFieldProcessorV1();
        baseProcessor.setIsPreDeduplication(false);

        String outFilePath = CommonProcessor.BASE_PATH + "EFP_" + baseProcessor.getKeyName() + "_" + System.currentTimeMillis() + ".xlsx";
        baseProcessor.setOutFilePath(outFilePath);

        List<List<FieldInfo>> fixedGroups = baseProcessor.doProcess();

        List<FieldInfo> essentialRows = new ArrayList<>();
        List<List<FieldInfo>> businessGroups = new ArrayList<>();

        for (List<FieldInfo> group : fixedGroups) {

            List<FieldInfo> normalList = new ArrayList<>();
            for (FieldInfo fieldInfo : group) {
                if ("是".equals(fieldInfo.getEssentialField())) {
                    essentialRows.add(fieldInfo);
                } else {
                    normalList.add(fieldInfo);
                }
            }

            businessGroups.add(normalList);
        }

        List<FieldInfo> essentialSelectRows = CommonProcessor.deduplicateRows(essentialRows);
        log.info("deduplicateGroups finish, essentialRows size: {}", essentialSelectRows.size());

        List<List<FieldInfo>> deduplicateNormalGroups = CommonProcessor.deduplicateGroups(businessGroups);
        log.info("deduplicateGroups start, tempNormalGroups size: {}", deduplicateNormalGroups.size());

        // 初始化抽样数量
        int businessTargetNum = baseProcessor.targetNum - essentialSelectRows.size();
        log.info("init businessTargetNum: {}", businessTargetNum);

        int[] sampleCounts = CommonProcessor.calcEachGroupSampleNum(deduplicateNormalGroups, businessTargetNum);
        log.info("calcEachGroupSampleNum finish, sampleCounts: {}", sampleCounts.length);

        // 抽样
        List<FieldInfo> selectedRows = CommonProcessor.doSampling(
                businessGroups, sampleCounts, baseProcessor.nameField, baseProcessor.commentField, businessTargetNum);

        log.info("doSampling finish, size: {}", selectedRows.size());

        essentialSelectRows.addAll(selectedRows);
        log.info("add selectedRows, total size: {}", essentialSelectRows.size());

        // 表基础字段放大数量
        // 最多添加 10 条
        // 在抽样完成后，新增以下逻辑
//        Map<String, Integer> fieldCount = countFieldOccurrences(
//                essentialRows,
//                baseProcessor.getNameField(),
//                baseProcessor.getCommentField()
//        );
//
//        List<FieldInfo> duplicatedRows = generateDuplicateData(
//                essentialSelectRows,
//                fieldCount,
//                baseProcessor.getNameField(),
//                baseProcessor.getCommentField(),
//                maxDuplicates);

        // 合并数据
//        essentialSelectRows.addAll(duplicatedRows);

        CommonProcessor.writeToExcel(essentialSelectRows, baseProcessor.getSheetName(), baseProcessor.getSheetName(), baseProcessor.inputFilePath, outFilePath);
        log.info("writeToExcel finish, path: {}", outFilePath);
    }

    /**
     * 为重复比例高的字段生成重复数据（基于档位放大）
     */
    private static List<FieldInfo> generateDuplicateData(
            List<FieldInfo> essentialSelectRows,
            Map<String, Integer> fieldCount,
            String nameField,
            String commentField,
            int maxDuplicates) {

        List<FieldInfo> duplicatedRows = new ArrayList<>();

        // 获取最大重复次数
        int maxCount = fieldCount.values().stream().mapToInt(Integer::intValue).max().orElse(1);

        for (FieldInfo fieldInfo : essentialSelectRows) {
            String key = CommonProcessor.getKey(fieldInfo);
            int count = fieldCount.getOrDefault(key, 1);

            if (count > 1) {
                int bucket = getBucket(count, maxCount);
                double factor = DUPLICATE_FACTORS[bucket];

                int duplicates = Math.min(maxDuplicates, (int) Math.round(BUCKET_COUNT * factor));
                log.info("generateDuplicateData, key: {}, count: {}, bucket: {}, factor: {}, duplicates: {}", key, count, bucket, factor, duplicates);

                duplicates = Math.max(1, duplicates);

                for (int i = 0; i < duplicates; i++) {
                    FieldInfo duplicateFieldInfo = BeanUtil.copyProperties(fieldInfo, FieldInfo.class);
                    duplicateFieldInfo.setEssentialField("是 (重复)");
                    duplicatedRows.add(duplicateFieldInfo);
                }
            }
        }

        return duplicatedRows;
    }

    /**
     * 统计每个字段的出现次数
     */
    private static Map<String, Integer> countFieldOccurrences(List<FieldInfo> fieldInfos, String nameField, String commentField) {

        Map<String, Integer> fieldCount = new HashMap<>();
        for (FieldInfo fieldInfo : fieldInfos) {
            String key = CommonProcessor.getKey(fieldInfo);
            fieldCount.put(key, fieldCount.getOrDefault(key, 0) + 1);
        }

        return fieldCount;
    }

    /**
     * 将字段按重复次数分到 10 个档位中
     */
    private static int getBucket(int count, int maxCount) {
        if (maxCount <= 0) return 0;
        double ratio = (double) count / maxCount;
        int bucket = (int) Math.floor(ratio * BUCKET_COUNT);
        return Math.max(0, Math.min(bucket, BUCKET_COUNT - 1));
    }

}