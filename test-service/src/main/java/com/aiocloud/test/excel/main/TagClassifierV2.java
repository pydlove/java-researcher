package com.aiocloud.test.excel.main;

import com.aiocloud.test.excel.main.CommonProcessor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 对元数据统计分类标签是第几级
 * @description: TagClassifierV2.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-07-22 14:31 
 */
public class TagClassifierV2 {

    public static void main(String[] args) {

        String sourceFilePath = CommonProcessor.BASE_PATH + "担保业务.xlsx";
        String metadataFilePath = CommonProcessor.BASE_PATH + "0717优化_test0721_1753085390877.xlsx";
        String outputFilePath = CommonProcessor.BASE_PATH + "0717优化_test0721_1753085390877_" + System.currentTimeMillis() + ".xlsx";

        try {
            // 1. 读取源文件并构建标签层级Map
            Map<Integer, Map<String, Map<String, Object>>> tagMaps = readAndClassifyTags(sourceFilePath);

            // 2. 读取推荐标签并分类
            List<Map<String, Object>> classifiedTags = classifyRecommendedTags(metadataFilePath, tagMaps);

            // 3. 写入新Excel文件（按照指定格式）
            writeToNewExcelWithCustomColumns(outputFilePath, classifiedTags);

            System.out.println("处理完成，结果已写入: " + outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 读取源文件并构建标签层级Map（与之前相同）
    private static Map<Integer, Map<String, Map<String, Object>>> readAndClassifyTags(String filePath) throws IOException {
        Map<Integer, Map<String, Map<String, Object>>> tagMaps = new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            tagMaps.put(i, new HashMap<>());
        }

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("分类标签库");

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String tagName = getCellValue(row.getCell(1)); // 标签名称
                String tagLevelStr = getCellValue(row.getCell(2)); // 标签层级
                int tagLevel = parseTagLevel(tagLevelStr);

                if (tagLevel < 1 || tagLevel > 4) continue;

                Map<String, Object> tagInfo = new HashMap<>();
                tagInfo.put("tagName", tagName);
                tagInfo.put("tagLevel", tagLevel);
                tagInfo.put("parentTag", getCellValue(row.getCell(4)));
                tagInfo.put("dataLevel", getCellValue(row.getCell(5)));
                tagInfo.put("tagOrder", getCellValue(row.getCell(6)));
                tagInfo.put("isSensitive", getCellValue(row.getCell(7)));
                tagInfo.put("synonyms", getCellValue(row.getCell(8)));

                tagMaps.get(tagLevel).put(tagName, tagInfo);
            }
        }

        return tagMaps;
    }

    // 分类推荐标签（与之前相同）
    private static List<Map<String, Object>> classifyRecommendedTags(
            String filePath,
            Map<Integer, Map<String, Map<String, Object>>> tagMaps) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("字段核验信息");
            int recommendedTagCol = 8;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String recommendedTag = getCellValue(row.getCell(recommendedTagCol));
                if (recommendedTag == null || recommendedTag.isEmpty()) continue;

                int foundLevel = 0;
                for (int level = 1; level <= 4; level++) {
                    if (tagMaps.get(level).containsKey(recommendedTag)) {
                        foundLevel = level;
                        break;
                    }
                }

                if (foundLevel > 0) {
                    Map<String, Object> classifiedTag = new HashMap<>();
                    // 保留所有原始数据
                    for (int col = 0; col < 10; col++) { // 假设前9列是需要保留的数据
                        classifiedTag.put("col_" + col, getCellValue(row.getCell(col)));
                    }
                    classifiedTag.put("classifiedLevel", "L" + foundLevel);

                    result.add(classifiedTag);
                }
            }
        }

        return result;
    }

    // 按照指定格式写入新Excel文件
    private static void writeToNewExcelWithCustomColumns(
            String outputFilePath,
            List<Map<String, Object>> classifiedTags) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("分类结果");

            // 假设"0717优化_test0715_1752828817949.xlsx"的列名如下（请根据实际文件调整）
            String[] customHeaders = {
                    "资产名称", "实例名称", "表id", "表名称",
                    "表注释", "字段id", "字段名称", "字段注释",
                    "推荐标签", "是否基础字段", "分类标签等级"  // 新增最后一列
            };

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < customHeaders.length; i++) {
                headerRow.createCell(i).setCellValue(customHeaders[i]);
            }

            // 写入数据行
            int rowNum = 1;
            for (Map<String, Object> tag : classifiedTags) {
                Row row = sheet.createRow(rowNum++);

                // 按照customHeaders的顺序写入数据
                // 前9列来自原始数据（col_0到col_8）
                for (int col = 0; col < 10; col++) {
                    row.createCell(col).setCellValue(tag.get("col_" + col).toString());
                }

                // 第11列：新增的分类标签等级（L1-L4）
                row.createCell(10).setCellValue(tag.get("classifiedLevel").toString());
            }

            // 自动调整列宽
            for (int i = 0; i < customHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 写入文件
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                workbook.write(fos);
            }
        }
    }

    // 辅助方法（与之前相同）
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private static int parseTagLevel(String tagLevelStr) {
        if (tagLevelStr == null || tagLevelStr.isEmpty()) return 0;
        if (tagLevelStr.contains("级")) {
            return Integer.parseInt(tagLevelStr.replace("级", "").trim());
        }
        try {
            return Integer.parseInt(tagLevelStr.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
