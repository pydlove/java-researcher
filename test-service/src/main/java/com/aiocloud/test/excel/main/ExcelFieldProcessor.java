//package com.aiocloud.test.excel;
//
//import cn.hutool.core.util.StrUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.commons.text.similarity.LevenshteinDistance;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//public class ExcelFieldProcessor {
//
//    public static void main(String[] args) throws Exception {
//
//        // 构建正确的文件路径
//        String excelFileName = "test0715.xlsx";
//        String inputFilePath = CommonProcessor.BASE_PATH + excelFileName;
//
//        System.out.println("尝试读取文件: " + inputFilePath);
//
//        // 输出文件路径
//        String outputFilePath = CommonProcessor.BASE_PATH + "processed_fields_" + System.currentTimeMillis() + ".xlsx";
//
//        // Process the Excel file
//        processExcelFields(inputFilePath, outputFilePath);
//    }
//
//    public static void processExcelFields(String inputPath, String outputPath) throws Exception {
//
//        // Read the input Excel file
//        FileInputStream fis = new FileInputStream(inputPath);
//        Workbook workbook = WorkbookFactory.create(fis);
//
//        // Get the second sheet (0-based index)
//        Sheet sheet = workbook.getSheetAt(1);
//
//        // Collect all rows (skip header if needed)
//        List<Map<String, String>> rows = new ArrayList<>();
//        List<String> headers = CommonProcessor.getHeaders(sheet);
//
//        log.info("Complete reading excel");
//
//        // Read data rows
//        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//            Row row = sheet.getRow(i);
//            if (row == null) continue;
//
//            Map<String, String> rowData = new LinkedHashMap<>();
//            for (int j = 0; j < headers.size(); j++) {
//                Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
//                String value = "";
//                switch (cell.getCellType()) {
//                    case STRING:
//                        value = cell.getStringCellValue();
//                        break;
//                    case NUMERIC:
//                        value = String.valueOf(cell.getNumericCellValue());
//                        break;
//                    case BOOLEAN:
//                        value = String.valueOf(cell.getBooleanCellValue());
//                        break;
//                    default:
//                        value = "";
//                }
//
//                log.info(" Read data for each column, index: {}/{}, column: {}, value: {}", j, i, headers.get(j), value);
//                rowData.put(headers.get(j), value);
//            }
//            rows.add(rowData);
//        }
//
//        log.info("Convert data to map to complete");
//
//        // Step 1: 优先挑选字段注释全是数字或字母的字段，并按字段名称+字段注释去重
//        Set<String> seenKeys = new HashSet<>();
//        List<Map<String, String>> numericOrAlphaRows = rows.stream()
//                .filter(row -> {
//                    String comment = row.get("字段注释");
//                    return comment != null && comment.matches("^[a-zA-Z0-9]+$");
//                })
//                .filter(row -> {
//                    String key = row.get("字段名称") + "|" + row.get("字段注释");
//                    return seenKeys.add(key); // 只有首次出现的key会返回true
//                })
//                .collect(Collectors.toList());
//
//        int R = numericOrAlphaRows.size();
//        log.info("Found {} unique rows with numeric or alphabetic comments", R);
//
//        // 剩余的记录用于相似度分组（排除已选中的记录）
//        List<Map<String, String>> remainingRows = rows.stream()
//                .filter(row -> {
//                    String key = row.get("字段名称") + "|" + row.get("字段注释");
//                    return !seenKeys.contains(key);
//                })
//                .collect(Collectors.toList());
//
//        log.info("{} rows remaining for similarity grouping", remainingRows.size());
//
//        // Group by similarity of field name + field comment
//        boolean isOnlyFieldNameMatch = true;
//        List<List<Map<String, String>>> similarityGroups = groupBySimilarity(remainingRows, isOnlyFieldNameMatch, "字段名称", "字段注释");
//        log.info("Group by similarity complete, size: {}", similarityGroups.size());
//
//        int targetNum = 1000;
//        int targetCount = targetNum / 10;
//        int smallGroupThreshold = rows.size() / targetCount;
//        List<List<Map<String, String>>> fixedGroups = forceMergeSmallGroups(similarityGroups, targetCount, smallGroupThreshold);
//        log.info("Merged into {} fixed groups", fixedGroups.size());
//
//        // Divide into 1000 groups
//        // List<Map<String, String>> dividedGroups = smartDivideGroups(similarityGroups, 1000);
//        // log.info("Divide into groups complete");
//        // Step 3: 按组大小降序排序
//        fixedGroups.sort((g1, g2) -> Integer.compare(g2.size(), g1.size()));
//
//        // Step 4: 从每个组取一条记录，循环反复，直到满足总数 R+S ≥ 1000
//        List<Map<String, String>> selectedRows = new ArrayList<>(numericOrAlphaRows);
//        int S = 0;
//
//        while (selectedRows.size() < targetCount && !fixedGroups.isEmpty()) {
//            // 遍历所有组，每组取一条（如果组不为空）
//            for (List<Map<String, String>> group : fixedGroups) {
//                if (!group.isEmpty()) {
//                    selectedRows.add(group.remove(0));
//                    S++;
//                    if (selectedRows.size() >= targetCount) {
//                        break;
//                    }
//                }
//            }
//
//            // 移除空组
//            fixedGroups.removeIf(List::isEmpty);
//        }
//
//        log.info("Selected {} rows from numeric/alpha comments and {} rows from similarity groups", R, S);
//
//        // Write to output Excel file
//        CommonProcessor.writeToExcel(selectedRows, 1, inputPath, outputPath);
//        log.info("Write to excel complete");
//
//        // Close resources
//        fis.close();
//        workbook.close();
//
//        System.out.println("Processing completed. Output written to: " + outputPath);
//    }
//
//    private static List<List<Map<String, String>>> forceMergeSmallGroups(
//            List<List<Map<String, String>>> similarityGroups,
//            int targetGroupCount,
//            int smallGroupThreshold) {
//
//        // Step 1: 分离大小组
//        List<List<Map<String, String>>> largeGroups = new ArrayList<>();
//        List<List<Map<String, String>>> smallGroups = new ArrayList<>();
//
//        for (List<Map<String, String>> group : similarityGroups) {
//            if (group.size() >= smallGroupThreshold) {
//                largeGroups.add(group);
//            } else {
//                smallGroups.add(group);
//            }
//        }
//
//        log.info("Large groups count: {}", largeGroups.size());
//        log.info("Small groups count: {}", smallGroups.size());
//
//        // Step 2: 多轮合并最小的两个组，直到满足目标组数
//        while (smallGroups.size() + largeGroups.size() > targetGroupCount && smallGroups.size() > 1) {
//
//            // 按大小排序，最小的在前
//            smallGroups.sort(Comparator.comparingInt(List::size));
//
//            List<Map<String, String>> groupA = smallGroups.get(0);
//            List<Map<String, String>> groupB = smallGroups.get(1);
//
//            List<Map<String, String>> mergedGroup = new ArrayList<>(groupA);
//            mergedGroup.addAll(groupB);
//
//            smallGroups.remove(groupA);
//            smallGroups.remove(groupB);
//            smallGroups.add(mergedGroup);
//        }
//
//        // Step 3: 返回结果
//        List<List<Map<String, String>>> finalGroups = new ArrayList<>(largeGroups);
//        finalGroups.addAll(smallGroups);
//
//        return finalGroups;
//    }
//
//    private static List<List<Map<String, String>>> groupBySimilarity(
//            List<Map<String, String>> rows, boolean isOnlyFieldNameMatch, String nameField, String commentField) {
//
//        LevenshteinDistance distance = new LevenshteinDistance();
//        double similarityThreshold = 0.7; // Adjust this threshold as needed
//
//        List<List<Map<String, String>>> groups = new ArrayList<>();
//
//        int i = 0;
//        for (Map<String, String> row : rows) {
//
//            i++;
//            String name = row.get(nameField);
//            String comment = row.get(commentField);
//            if (StrUtil.isBlank(comment)) {
//                continue;
//            }
//
//            String combined = name;
//            if (!isOnlyFieldNameMatch) {
//                combined = (name != null ? name : "") + " " + (comment != null ? comment : "");
//            }
//
//            boolean added = false;
//
//            int j = 0;
//            for (List<Map<String, String>> group : groups) {
//
//                j++;
//                // Compare with first element in group
//                String groupName = group.get(0).get(nameField);
//                String groupComment = group.get(0).get(commentField);
//                if (StrUtil.isBlank(groupComment)) {
//                    continue;
//                }
//
//                String groupCombined = groupName != null ? groupName : "";
//                if (!isOnlyFieldNameMatch) {
//                    groupCombined = (groupName != null ? groupName : "") + " " + (groupComment != null ? groupComment : "");
//                }
//
//                // Calculate similarity
//                int maxLength = Math.max(combined.length(), groupCombined.length());
//                if (maxLength == 0) continue;
//
//                double similarity = 1 - (distance.apply(combined, groupCombined) / (double) maxLength);
//
//                if (similarity >= similarityThreshold) {
//                    log.info("Similarity: {}, name: {}, comment: {}, index: {}/{}", similarity, name, comment, j, i);
//                    group.add(row);
//                    added = true;
//                    break;
//                }
//            }
//
//            if (!added) {
//                List<Map<String, String>> newGroup = new ArrayList<>();
//                newGroup.add(row);
//                groups.add(newGroup);
//            }
//        }
//
//        return groups;
//    }
//
//    private static List<Map<String, String>> smartDivideGroups(
//            List<List<Map<String, String>>> similarityGroups, int targetGroupCount) {
//
//        // 按组大小降序排序（相似度高的组优先）
//        similarityGroups.sort((g1, g2) -> Integer.compare(g2.size(), g1.size()));
//
//        List<Map<String, String>> resultGroups = new ArrayList<>();
//
//        // 初始化1000个空组
//        for (int i = 0; i < targetGroupCount; i++) {
//
//            int j = i % similarityGroups.size();
//            int k = i / similarityGroups.size();
//            Map<String, String> res = similarityGroups.get(j).get(k);
//            resultGroups.add(res);
//        }
//
//        return resultGroups;
//    }
//}
