package com.aiocloud.test.excel.main;

import cn.hutool.core.bean.BeanUtil;
import com.aiocloud.test.excel.base.FieldInfo;
import com.aiocloud.test.excel.base.TrainMetadataGeneratorParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aiocloud.test.excel.main.PipelineGenerator.METADATA_SHEET_NAME;
import static com.aiocloud.test.excel.main.PipelineGenerator.SEQ;

/**
 * 基于元数据抽取训练集
 * @description: ExcelFieldProcessorV1.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-07-22 14:29 
 */
@Data
@Slf4j
public class TrainMetadataGenerator extends BaseProcessor {

    private static final double[] DUPLICATE_FACTORS = new double[]{
            0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0
    };

    private static final int BUCKET_COUNT = 10; // 10 个档位

    public static void main(String[] args) throws Exception {

//        String metadataFileName = "0717优化（基础元数据）.xlsx";
//        String metadataFilePath = CommonProcessor.BASE_PATH +
//                "metadata" + File.separator + metadataFileName;
//
//        String metadataSheetName = "字段核验信息";
//
//        int targetNum = 200;
//
//        generate(false, metadataFileName, metadataFilePath, metadataSheetName, targetNum, null);
    }

    public static String generate(TrainMetadataGeneratorParam trainMetadataGeneratorParam) throws Exception {

        String trainFileName = trainMetadataGeneratorParam.getTrainFileName();
        String testFileName = trainMetadataGeneratorParam.getTestFileName();
        Integer targetNum = trainMetadataGeneratorParam.getTargetNum();
        Boolean openFiledRuleAndAiSlice = trainMetadataGeneratorParam.getOpenFiledRuleAndAiSlice();
        String metadataFileName = trainMetadataGeneratorParam.getMetadataFileName();
        String metadataFilePath = trainMetadataGeneratorParam.getMetadataFilePath();

        int maxDuplicates = 10;

        BaseProcessor baseProcessor = new TrainMetadataGenerator();
        baseProcessor.setIsPreDeduplication(false);

        String keyName = metadataFileName.replace(".xlsx", "");
        String currentTrainPath = keyName + "_" + System.currentTimeMillis();
        String outFilePath = CommonProcessor.BASE_PATH +
                "train" + File.separator + "main" + File.separator +
                "train_" + currentTrainPath + ".xlsx";

        baseProcessor.setOutFilePath(outFilePath);

        List<FieldInfo> metadataFieldList = baseProcessor.readMetadata(metadataFilePath, METADATA_SHEET_NAME);

        List<FieldInfo> essentialRows = new ArrayList<>();
        List<FieldInfo> businessRows = new ArrayList<>();
        for (FieldInfo fieldInfo : metadataFieldList) {

            List<FieldInfo> normalList = new ArrayList<>();
            if ("是".equals(fieldInfo.getEssentialField())) {
                essentialRows.add(fieldInfo);
            } else {
                businessRows.add(fieldInfo);
            }
        }

        List<List<FieldInfo>> businessGroups = baseProcessor.doGrouping(businessRows, targetNum);

        List<FieldInfo> essentialSelectRows = CommonProcessor.deduplicateRows(essentialRows);
        log.info("deduplicateGroups finish, essentialRows size: {}", essentialSelectRows.size());

        // 初始化抽样数量
        int businessTargetNum = targetNum - essentialSelectRows.size();
        log.info("init businessTargetNum: {}", businessTargetNum);

        List<FieldInfo> samplingFieldList = baseProcessor.doSampling(businessGroups, businessTargetNum);
        log.info("doSampling finish, size: {}", samplingFieldList.size());

        essentialSelectRows.addAll(samplingFieldList);
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

        // 训练集元数据
        CommonProcessor.writeToExcel(
                essentialSelectRows,
                METADATA_SHEET_NAME,
                METADATA_SHEET_NAME,
                metadataFilePath,
                outFilePath
        );

        log.info("writeToExcel finish, path: {}", outFilePath);

        // 训练集和测试集
        if (!openFiledRuleAndAiSlice) {

            generateTestFile(essentialSelectRows, metadataFieldList, testFileName, baseProcessor);

            String trainFilePath = CommonProcessor.BASE_PATH + "train" + File.separator + "target" + File.separator + trainFileName;
            baseProcessor.generateTrainFile(essentialSelectRows, trainFilePath);
        }

        return outFilePath;
    }

    private static void generateTestFile(List<FieldInfo> essentialSelectRows, List<FieldInfo> metadataFieldList, String testFileName, BaseProcessor baseProcessor) throws Exception {

        Map<String, String> trainFieldMap = new HashMap<>();
        for (FieldInfo fieldInfo : essentialSelectRows) {
            String key = getFieldKey(fieldInfo);
            trainFieldMap.put(key, fieldInfo.getFieldName());
        }

        // 测试集元数据
        List<FieldInfo> testMetadataFieldList = new ArrayList<>();
        for (FieldInfo fieldInfo : metadataFieldList) {
            String key = getFieldKey(fieldInfo);
            if (!trainFieldMap.containsKey(key)) {
                testMetadataFieldList.add(fieldInfo);
            }
        }

        String testFilePath = CommonProcessor.BASE_PATH + "train" + File.separator + "test" + File.separator + testFileName;
        baseProcessor.generateTrainFile(testMetadataFieldList, testFilePath);
    }

    private static String getFieldKey(FieldInfo fieldInfo) {
        String key = fieldInfo.getInstanceName() + "-" + fieldInfo.getTableName() + "-" + fieldInfo.getFieldName();
        return key;
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