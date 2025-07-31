package com.aiocloud.test.excel.main;

import com.aiocloud.test.excel.base.FieldInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 对元数据进行优化
 * @description: FieldCommentVerifier.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-07-22 14:29 
 */
@Slf4j
public class MetadataOptimization {


    private static final String STRATEGY_SIMILARITY = "Similarity";
    private static final String STRATEGY_BASE_FIELD = "baseField";

    /**
     * 1. 字段注释看有没有相似的字段，通过相似字段补上空的注释
     * 2. 表注释为空的，直接移除（应该让用户补充）
     *
     * @since 1.0.0
     *
     * @param: args
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-07-22 14:46 
     */
    public static void main(String[] args) {

        // String inputFilePath = CommonProcessor.BASE_PATH + "政务数据_0718.xlsx";
        // String outputFilePath = CommonProcessor.BASE_PATH + "政务数据_0718_" + System.currentTimeMillis() + ".xlsx";

         String inputFilePath = CommonProcessor.BASE_PATH + "test0715.xlsx";
         String outputFilePath = CommonProcessor.BASE_PATH + "0717优化_test0721_" + System.currentTimeMillis() + ".xlsx";

        BaseProcessor baseProcessor = new TrainMetadataGenerator();
        baseProcessor.setIsPreDeduplication(false);

//        String inputFilePath = CommonProcessor.BASE_PATH + "（金融）全量训练数据测试0725.xlsx";
//        String outputFilePath = CommonProcessor.BASE_PATH + "（金融）全量训练数据测试0725_" + System.currentTimeMillis() + ".xlsx";

        String strategyName = STRATEGY_BASE_FIELD;

        try {
            // Read input Excel file
            FileInputStream fis = new FileInputStream(inputFilePath);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("字段核验信息");

            // Group data by instance name
            Map<String, List<FieldInfo>> instanceGroups = getInstanceMap(sheet, 20000);

            // Process each instance group
            List<FieldInfo> allFields = new ArrayList<>();

            switch (strategyName) {
                case STRATEGY_SIMILARITY:
                    allFields = handleBySimilarity(instanceGroups, baseProcessor);
                    break;
                case STRATEGY_BASE_FIELD:
                default:
                    allFields = handleByTableBaseField(instanceGroups);
                    break;
            }

            // Write to output Excel file
            writeOutputFile(allFields, outputFilePath);

            System.out.println("Processing completed. Output written to: " + outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<FieldInfo> handleBySimilarity(Map<String, List<FieldInfo>> instanceGroups, BaseProcessor baseProcessor) {

        List<FieldInfo> allFields = new ArrayList<>();

        instanceGroups.forEach((instanceName, fieldInfos) -> {
            List<List<FieldInfo>> similarityGroups = CommonProcessor.groupBySimilarity(fieldInfos, baseProcessor.getIsOnlyFieldNameMatch(), baseProcessor.getSimilarityThreshold());

            for (List<FieldInfo> singleGroupFieldInfos : similarityGroups) {
                String mostCommonComment = findMostCommonComment(singleGroupFieldInfos);
                for (FieldInfo singleGroupFieldInfo : singleGroupFieldInfos) {
                    singleGroupFieldInfo.setFieldComment(mostCommonComment);
                }

                allFields.addAll(singleGroupFieldInfos);
            }
        });


        return allFields;
    }

    private static List<FieldInfo> handleByTableBaseField(Map<String, List<FieldInfo>> instanceGroups) {

        List<FieldInfo> allFields = new ArrayList<>();
        Map<String, List<FieldInfo>> optimizeMap = new HashMap<>();

        for (Map.Entry<String, List<FieldInfo>> entry : instanceGroups.entrySet()) {

            String instanceName = entry.getKey();
            List<FieldInfo> fieldInfos = entry.getValue();

            // Get unique table names count
            long uniqueTableCount = fieldInfos.stream()
                    .map(FieldInfo::getTableName)
                    .distinct()
                    .count();

            // Group fields by field name
            Map<String, List<FieldInfo>> fieldNameGroups = fieldInfos.stream()
                    .collect(Collectors.groupingBy(FieldInfo::getFieldName));

            // Process each field name group
            for (Map.Entry<String, List<FieldInfo>> fieldEntry : fieldNameGroups.entrySet()) {

                String fieldName = fieldEntry.getKey();
                List<FieldInfo> sameNameFields = fieldEntry.getValue();

                // Check if duplicates >= 50% of unique table count
                if (uniqueTableCount > 5 && sameNameFields.size() >= uniqueTableCount * 0.5) {

                    Map<String, Long> commentCounts = fieldInfos.stream()
                            .collect(Collectors.groupingBy(FieldInfo::getFieldComment, Collectors.counting()));

                    if (commentCounts.size() > 1) {

                        String mostCommonComment = findMostCommonComment(sameNameFields);

                        log.info("instanceName: {}, uniqueTableCount: {}, fieldSize: {}, most common comment for field: {} is: {} ", instanceName, uniqueTableCount, sameNameFields.size(), fieldName, mostCommonComment);

                        // Update all fields with this name to use the most common comment
                        for (FieldInfo field : sameNameFields) {
                            field.setFieldComment(mostCommonComment);
                        }
                    }

                    FieldInfo fieldInfo = sameNameFields.get(0);
                    List<FieldInfo> subFieldInfos = optimizeMap.computeIfAbsent(fieldInfo.getFieldName(), key -> new ArrayList<>());
                    subFieldInfos.add(fieldInfo);
                }

                allFields.addAll(sameNameFields);
            }

        }

        log.info("Processing completed. Optimized field count: {}", allFields.size());
        unifyFieldCommentsByFieldName(allFields, optimizeMap);
        log.info("after unifyFieldCommentsByFieldName, field count: {}", allFields.size());

        return allFields;
    }

    private static Map<String, List<FieldInfo>> getInstanceMap(Sheet sheet, Integer endRowNum) {

        // Group data by instance name
        Map<String, List<FieldInfo>> instanceGroups = new HashMap<>();

        int targetNum = endRowNum == null ? sheet.getLastRowNum() : endRowNum;

        // Read all rows (skip header)
        for (int i = 1; i <= targetNum; i++) {

            FieldInfo fieldInfo = CommonProcessor.getFieldMetadata(sheet, i);
            if (fieldInfo == null) continue;

            instanceGroups.computeIfAbsent(fieldInfo.getInstanceName(), k -> new ArrayList<>()).add(fieldInfo);
        }

        return instanceGroups;
    }

    private static void unifyFieldCommentsByFieldName(List<FieldInfo> allFields, Map<String, List<FieldInfo>> optimizeMap) {

        Map<String, FieldInfo> finalOptimizeMap = new HashMap<>();

        for (Map.Entry<String, List<FieldInfo>> entry : optimizeMap.entrySet()) {
            String fieldName = entry.getKey();
            List<FieldInfo> optimizeFields = entry.getValue();

            String mostCommonComment = optimizeFields.stream()
                    .collect(Collectors.groupingBy(FieldInfo::getFieldComment, Collectors.counting()))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("");

            for (FieldInfo optimizeField : optimizeFields) {
                optimizeField.setFieldComment(mostCommonComment);

                String key = getKey(optimizeField.getInstanceName(), fieldName);
                finalOptimizeMap.put(key, optimizeField);
            }

        }

        Map<String, Integer> statisticsNumMap = new HashMap<>();
        Map<String, String> statisticsCommentMap = new HashMap<>();
        for (FieldInfo field : allFields) {

            String instanceName = field.getInstanceName();
            String fieldName = field.getFieldName();

            String key = getKey(instanceName, fieldName);
            if (finalOptimizeMap.containsKey(key)) {

                Integer i = statisticsNumMap.computeIfAbsent(fieldName, k -> 0);
                statisticsNumMap.put(fieldName, i + 1);

                String fieldComment = finalOptimizeMap.get(key).getFieldComment();
                field.setFieldComment(fieldComment);
                field.setEssentialField("是");

                statisticsCommentMap.putIfAbsent(fieldName, fieldComment);
            } else {
                field.setEssentialField("否");
            }
        }

        List<Map.Entry<String, Integer>> sortedEntries = statisticsNumMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        AtomicInteger total = new AtomicInteger();
        sortedEntries.forEach(entry -> {

            String fieldName = entry.getKey();
            int count = entry.getValue();
            String comment = statisticsCommentMap.get(fieldName);
            total.addAndGet(count);

            log.info("统一字段描述：字段名 [{}], 统一后的字段描述为 [{}], size: {}", fieldName, comment, count);
        });

        log.info("统一字段描述：总数为: {}", total);
    }

    private static String getKey(String instanceName, String fieldName) {
        String key = instanceName + "-" + fieldName;
        return key;
    }


    public static String findMostCommonComment(List<FieldInfo> fieldInfos) {

        Map<String, Long> commentCounts = fieldInfos.stream()
                .collect(Collectors.groupingBy(FieldInfo::getFieldComment, Collectors.counting()));

        return commentCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }

    private static void writeOutputFile(List<FieldInfo> fields, String outputPath) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("字段核验信息");

        // Create header row
        String[] headers = {"资产名称", "实例名称", "表id", "表名称", "表注释", "字段id", "字段名称", "字段注释", "推荐标签", "是否基础字段"};

        // Write header
        CommonProcessor.writeHeader(sheet, Arrays.asList(headers));

        // Write data rows
        CommonProcessor.writeDataRows(sheet, fields, Arrays.asList(headers));

        CommonProcessor.autoSizeColumns(sheet, headers.length);

        // Write to file
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            workbook.write(fos);
        }

        workbook.close();
    }
}