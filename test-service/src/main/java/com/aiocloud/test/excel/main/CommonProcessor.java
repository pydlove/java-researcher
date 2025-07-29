package com.aiocloud.test.excel.main;

import cn.hutool.core.util.StrUtil;
import com.aiocloud.test.excel.base.FieldInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class CommonProcessor {

    public static final String BASE_PATH = new File("").getAbsolutePath() + File.separator + "test-service" +
            File.separator + "src" + File.separator + "main" +
            File.separator + "java" + File.separator +
            "com" + File.separator + "aiocloud" + File.separator +
            "test" + File.separator + "excel" + File.separator + "file" + File.separator;

    public static List<List<FieldInfo>> deduplicateGroups(List<List<FieldInfo>> businessGroups) {

        List<List<FieldInfo>> result = new ArrayList<>();

        int i = 0;
        for (List<FieldInfo> group : businessGroups) {

            result.add(deduplicateRows(group));
        }

        return result;
    }

    public static List<FieldInfo> deduplicateRows(List<FieldInfo> fieldInfos) {

        List<FieldInfo> uniqueData = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>();
        for (int i = 0; i < fieldInfos.size(); i++) {

            FieldInfo fieldInfo = fieldInfos.get(i);

            String key = fieldInfo.getFieldName() + "|" + fieldInfo.getFieldComment();
            if (seenKeys.add(key)) {
                uniqueData.add(fieldInfo);
                // log.info("group: {}, unique row found: {}, 是否基础字段：{}", i, row.get(nameField), row.get("是否基础字段"));
            }
        }

        return uniqueData;
    }

    public static List<List<FieldInfo>> forceMergeSmallGroups(
            List<List<FieldInfo>> similarityGroups, int targetGroupCount, int smallGroupThreshold) {

        // Step 1: 分离大小组
        List<List<FieldInfo>> largeGroups = new ArrayList<>();
        List<List<FieldInfo>> smallGroups = new ArrayList<>();

        for (List<FieldInfo> group : similarityGroups) {
            if (group.size() >= smallGroupThreshold) {
                largeGroups.add(group);
            } else {
                smallGroups.add(group);
            }
        }

        log.info("Large groups count: {}", largeGroups.size());
        log.info("Small groups count: {}", smallGroups.size());

        // Step 2: 多轮合并最小的两个组，直到满足目标组数
        while (smallGroups.size() + largeGroups.size() > targetGroupCount && smallGroups.size() > 1) {

            // 按大小排序，最小的在前
            smallGroups.sort(Comparator.comparingInt(List::size));

            List<FieldInfo> groupA = smallGroups.get(0);
            List<FieldInfo> groupB = smallGroups.get(1);

            List<FieldInfo> mergedGroup = new ArrayList<>(groupA);
            mergedGroup.addAll(groupB);

            smallGroups.remove(groupA);
            smallGroups.remove(groupB);
            smallGroups.add(mergedGroup);
        }

        // Step 3: 返回结果
        List<List<FieldInfo>> finalGroups = new ArrayList<>(largeGroups);
        finalGroups.addAll(smallGroups);

        return finalGroups;
    }

    public static List<FieldInfo> readDatabaseMetadata(String filePath, String sheetName) throws IOException {

        List<FieldInfo> metadata = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in the workbook");
            }

            // 读取数据行
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {

                FieldInfo fieldInfo = getFieldMetadata(sheet, i);
                if (fieldInfo == null) continue;

                log.info(" Read data for each column, index: {}/{}, column value: {}", i, lastRowNum, fieldInfo.getFieldName());

                metadata.add(fieldInfo);
            }
        }

        return metadata;
    }

    public static FieldInfo getFieldMetadata(Sheet sheet, int i) {

        Row row = sheet.getRow(i);
        if (row == null) return null;

        String tableComment = getStringValue(row.getCell(4));
        if (StrUtil.isBlank(tableComment)) {
            log.info("table comment is empty, row: {}", i);
            return null;
        }

        String fieldValue = getStringValue(row.getCell(6));
        if (StrUtil.isBlank(fieldValue)) {
            log.info("field value is empty, row: {}", i);
            return null;
        }

        String recommendedTag = getStringValue(row.getCell(8));
        recommendedTag = recommendedTag.replace("，", ",");
        if (recommendedTag.contains(",")) {
            String[] split = recommendedTag.split(",");
            recommendedTag = split[0];
        }

        FieldInfo fieldInfo = new FieldInfo(
                getStringValue(row.getCell(0)),  // 资产名称
                getStringValue(row.getCell(1)),  // 实例名称
                getStringValue(row.getCell(2)),  // 表id
                getStringValue(row.getCell(3)),  // 表名称
                tableComment,  // 表注释
                getStringValue(row.getCell(5)),  // 字段id
                fieldValue,  // 字段名称
                getStringValue(row.getCell(7)),  // 字段注释
                recommendedTag,  // 推荐标签
                getStringValue(row.getCell(9))
        );

        return fieldInfo;
    }

    private static String getCellValueAsString(Cell cell) {

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    public static void writeToExcel(List<FieldInfo> data, String sheetName, String newSheetName, String inputFilePath, String outputPath) throws Exception {

        try (FileInputStream fis = new FileInputStream(inputFilePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in the workbook");
            }

            List<String> headers = getHeaders(sheet);

            try (Workbook outputWorkbook = new XSSFWorkbook()) {

                Sheet outputSheet = outputWorkbook.createSheet(newSheetName);

                // 写入表头
                writeHeader(outputSheet, headers);

                // 写入数据行
                writeDataRows(outputSheet, data, headers);

                // 自动调整列宽
                autoSizeColumns(outputSheet, headers.size());

                // 保存文件
                try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                    outputWorkbook.write(fos);
                }
            }
        }
    }

    public static void writeHeader(Sheet sheet, List<String> headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            headerRow.createCell(i).setCellValue(headers.get(i));
        }
        log.info("Create header row complete");
    }

    public static void writeDataRows(Sheet sheet, List<FieldInfo> fieldInfos, List<String> headers) {

        int rowNum = 1;
        for (FieldInfo fieldInfo : fieldInfos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(fieldInfo.getAssetName());
            row.createCell(1).setCellValue(fieldInfo.getInstanceName());
            row.createCell(2).setCellValue(fieldInfo.getTableId());
            row.createCell(3).setCellValue(fieldInfo.getTableName());
            row.createCell(4).setCellValue(fieldInfo.getTableComment());
            row.createCell(5).setCellValue(fieldInfo.getFieldId());
            row.createCell(6).setCellValue(fieldInfo.getFieldName());
            row.createCell(7).setCellValue(fieldInfo.getFieldComment());
            row.createCell(8).setCellValue(fieldInfo.getRecommendedTag());
            row.createCell(9).setCellValue(fieldInfo.getEssentialField());
        }

        log.info("Create data rows complete: {}", rowNum - 1);
    }

    public static void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            // sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, 5000);
        }
    }

    public static List<String> getHeaders(Sheet sheet) {

        List<String> headers = new ArrayList<>();

        // Read headers from first row
        Row headerRow = sheet.getRow(0);
        for (Cell cell : headerRow) {
            headers.add(cell.getStringCellValue());
        }
        return headers;
    }

    public static List<FieldInfo> doSampling(List<List<FieldInfo>> fixedGroups, int[] sampleCounts, String nameField, String commentField, int targetNum) {

        // 排序 + 等距抽样
        // 可考虑以下方式升级：
        // 字段向量化：使用 TF-IDF 或 Word2Vec 将字段名和注释转化为向量。
        // 构建相似度矩阵：计算字段间余弦相似度。
        // 聚类分析：使用层次聚类或 K-Means 进行分组。
        // 分层抽样：在每个簇中按比例抽取最具代表性的字段。
        List<FieldInfo> selectedRows = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>(); // 新增：记录已选字段

        for (int g = 0; g < fixedGroups.size(); g++) {
            List<FieldInfo> group = fixedGroups.get(g);
            int sampleCount = sampleCounts[g];
            int groupSize = group.size();

            log.info("Processing group: {}, groupSize: {}, sample count: {}", g, groupSize, sampleCount);

            if (sampleCount <= 0) continue;

            List<FieldInfo> sampledInGroup = new ArrayList<>();

            // Step 1: 等距抽样 + 去重
            int step = Math.max(1, groupSize / sampleCount);
            for (int i = 0; i < groupSize && sampledInGroup.size() < sampleCount; i++) {
                int index = Math.min(i * step, groupSize - 1);
                FieldInfo fieldInfo = group.get(index);

                addGroupIfNotAdd(fieldInfo, seenKeys, sampledInGroup);
            }

            // Step 2: 若仍未满足样本数，乱序顺序抽样
            if (sampledInGroup.size() < sampleCount) {
                List<FieldInfo> candidates = new ArrayList<>(group);
                Collections.shuffle(candidates); // 打乱顺序

                for (FieldInfo fieldInfo : candidates) {
                    String key = getKey(fieldInfo);
                    if (!seenKeys.contains(key)) {
                        seenKeys.add(key);
                        sampledInGroup.add(fieldInfo);
                        if (sampledInGroup.size() >= sampleCount) break;
                    }
                }
            }

            sampledInGroup.sort(Comparator.comparing(fieldInfo -> fieldInfo.getFieldComment()));

            selectedRows.addAll(sampledInGroup);
        }

        // 如果还有配额，顺序抽
        // 但要保证不重复
        for (int i = 0; i < fixedGroups.size(); i++) {
            List<FieldInfo> group = fixedGroups.get(i);
            for (FieldInfo fieldInfo : group) {

                if (selectedRows.size() >= targetNum) break;

                addGroupIfNotAdd(fieldInfo, seenKeys, selectedRows);
            }
        }

        return selectedRows;
    }

    public static String getKey(FieldInfo fieldInfo) {
        String key = fieldInfo.getFieldName() + "||" + fieldInfo.getFieldComment();
        return key;
    }

    private static void addGroupIfNotAdd(FieldInfo fieldInfo, Set<String> seenKeys, List<FieldInfo> sampledInGroup) {
        String key = getKey(fieldInfo);
        if (!seenKeys.contains(key)) {
            seenKeys.add(key);
            sampledInGroup.add(fieldInfo);
        }
    }

    public static int[] calcEachGroupSampleNum(List<List<FieldInfo>> deduplicateNormalGroups, int targetNum) {

        // 先统计每个组的原始数据大小
        int[] rawGroupSizes = deduplicateNormalGroups.stream()
                .mapToInt(group -> group.size())
                .toArray();

        return calcEachGroupSampleNumWithRawSize(deduplicateNormalGroups, rawGroupSizes, targetNum, 0.8, 0.2);
    }

    /**
     * 计算每组应抽取的样本数量（考虑原始数据量和去重后数据量）
     *
     * @param deduplicateNormalGroups     去重后的分组数据
     * @param rawGroupSizes   每个组的原始数据量（未去重）
     * @param targetNum       总目标抽样数
     * @param alpha           原始数据占比权重（推荐 0.7）
     * @param beta            去重后数据占比权重（推荐 0.3）
     * @return 每组应抽取的样本数数组
     */
    public static int[] calcEachGroupSampleNumWithRawSize(
            List<List<FieldInfo>> deduplicateNormalGroups,
            int[] rawGroupSizes,
            int targetNum,
            double alpha,
            double beta) {

        if (deduplicateNormalGroups.size() != rawGroupSizes.length) {
            throw new IllegalArgumentException("组的数量与原始数据大小数组长度不匹配");
        }

        int groupCount = deduplicateNormalGroups.size();
        int[] sampleCounts = new int[groupCount];
        int totalAllocated = 0;

        // 获取每个组的去重后大小
        int[] uniqueSizes = deduplicateNormalGroups.stream()
                .mapToInt(List::size)
                .toArray();

        // 计算每个组的加权得分
        double[] scores = new double[groupCount];
        for (int i = 0; i < groupCount; i++) {
            scores[i] = alpha * rawGroupSizes[i] + beta * uniqueSizes[i];
        }

        // 按照得分比例分配样本数
        double totalScore = Arrays.stream(scores).sum();
        for (int i = 0; i < groupCount; i++) {
            int count = Math.max(1, (int) Math.round(targetNum * scores[i] / totalScore));
            sampleCounts[i] = count;
            totalAllocated += count;
        }

        // 调整误差
        // 调整误差
        int diff = targetNum - totalAllocated;

        if (diff > 0) {
            // 优先给前 10% 的组增加样本（高权重组）
            int topN = Math.max(1, groupCount / 10); // 至少一个组
            while (diff > 0) {
                boolean allocated = false;
                for (int i = 0; i < topN && diff > 0; i++) {
                    sampleCounts[i]++;
                    diff--;
                    allocated = true;
                }
                if (!allocated) break; // 没有更多可分配的组
            }
        } else if (diff < 0) {
            // 从后半部分开始削减，至少保留 1 个
            int startFrom = groupCount / 2; // 从后半部分开始削减
            while (diff < 0) {
                boolean reduced = false;
                for (int i = startFrom; i < groupCount && diff < 0; i++) {
                    if (sampleCounts[i] > 1) {
                        sampleCounts[i]--;
                        diff++;
                        reduced = true;
                    }
                }
                if (!reduced) {
                    // 如果后半部分都减不动了，尝试从头开始削减
                    for (int i = 0; i < startFrom && diff < 0; i++) {
                        if (sampleCounts[i] > 1) {
                            sampleCounts[i]--;
                            diff++;
                            reduced = true;
                        }
                    }
                    if (!reduced) break; // 没法再减了
                }
            }
        }

        return sampleCounts;
    }


    public static List<List<FieldInfo>> groupBySimilarity(
            List<FieldInfo> fieldInfos, boolean isOnlyFieldNameMatch, double similarityThreshold) {

        LevenshteinDistance distance = new LevenshteinDistance();
        List<List<FieldInfo>> groups = new ArrayList<>();

        int i = 0;
        for (FieldInfo fieldInfo : fieldInfos) {

            i++;
            String fieldName = fieldInfo.getFieldName();
            String fieldComment = fieldInfo.getFieldComment();

            // 组合要比较的字符串
            String combined = isOnlyFieldNameMatch ? fieldName :
                    (fieldName != null ? fieldName : "") + " " + (fieldComment != null ? fieldComment : "");

            if (combined.trim().isEmpty()) {
                continue;
            }

            boolean added = false;

            log.info("field name: {}, comment: {}, index: {}", fieldName, fieldComment, i);

            // 与已有分组比较
            int j = 0;
            for (List<FieldInfo> group : groups) {

                j++;
                String groupFieldName = group.get(0).getFieldName();
                String groupFieldComment = group.get(0).getFieldComment();

                String groupCombined = isOnlyFieldNameMatch ? groupFieldName :
                        (groupFieldName != null ? groupFieldName : "") + " " + (groupFieldComment != null ? groupFieldComment : "");

                if (groupCombined.trim().isEmpty()) {
                    continue;
                }

                // 计算相似度
                int maxLength = Math.max(combined.length(), groupCombined.length());
                if (maxLength == 0) continue;

                double similarity = 1 - (distance.apply(combined, groupCombined) / (double) maxLength);
                // log.info("Similarity: {}, name: {}, comment: {}, index: {}/{}", similarity, name, comment, j, i);

                if (similarity >= similarityThreshold) {
                    group.add(fieldInfo);
                    added = true;
                    break;
                }
            }

            // 如果没有匹配的组，创建新组
            if (!added) {
                List<FieldInfo> newGroup = new ArrayList<>();
                newGroup.add(fieldInfo);
                groups.add(newGroup);
            }
        }

        return groups;
    }

    public static String getStringValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
