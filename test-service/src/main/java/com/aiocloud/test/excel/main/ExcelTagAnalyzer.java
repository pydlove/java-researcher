package com.aiocloud.test.excel.main;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计标签抽样占比情况
 * @description: ExcelTagAnalyzer.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-07-22 14:27
 */
public class ExcelTagAnalyzer {

    public static void main(String[] args) throws Exception {

        String excelFileName = "test0715.xlsx";
        String base = new File("").getAbsolutePath() + File.separator + "test-service" +
                File.separator + "src" + File.separator + "main" +
                File.separator + "java" + File.separator +
                "com" + File.separator + "aiocloud" + File.separator +
                "test" + File.separator + "excel" + File.separator;
        String inputFilePath = base + excelFileName;

        String outputFilePath = base + "Analyzer_" + System.currentTimeMillis() + ".xlsx";

        // 1. 读取两个sheet的数据
        Map<String, Integer> sheet2Tags = countTagsFromSheet(inputFilePath, 1); // 第二个sheet
        Map<String, Integer> sheet3Tags = countTagsFromSheet(inputFilePath, 2); // 第三个sheet

        // 2. 构建结果数据
        List<ResultData> resultDataList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sheet2Tags.entrySet()) {
            String tag = entry.getKey();
            int sheet2Count = entry.getValue();
            int sheet3Count = sheet3Tags.getOrDefault(tag, 0);
            double ratio = sheet2Count == 0 ? 0 : (double) sheet3Count / sheet2Count * 100;

            resultDataList.add(new ResultData(tag, sheet2Count, sheet3Count, ratio));
        }

        // 3. 按sheet2数据量降序排序
        resultDataList.sort((a, b) -> Integer.compare(b.getSheet2Count(), a.getSheet2Count()));

        // 4. 导出结果到Excel
        exportToExcel(outputFilePath, resultDataList);

        System.out.println("分析完成，结果已保存到: " + outputFilePath);
    }

    // 统计指定sheet中标签的出现次数
    private static Map<String, Integer> countTagsFromSheet(String filePath, int sheetIndex) throws Exception {
        Map<String, Integer> tagCountMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int tagColumnIndex = findTagColumnIndex(sheet);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 从第2行开始，跳过标题
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell cell = row.getCell(tagColumnIndex);
                if (cell == null) continue;

                String tag = getCellValueAsString(cell);
                if (tag != null && !tag.trim().isEmpty()) {
                    tagCountMap.put(tag, tagCountMap.getOrDefault(tag, 0) + 1);
                }
            }
        }

        return tagCountMap;
    }

    // 查找"推荐标签"列的索引
    private static int findTagColumnIndex(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return -1;

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && "推荐标签".equals(getCellValueAsString(cell))) {
                return i;
            }
        }
        return -1;
    }

    // 获取单元格值作为字符串
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    // 导出结果到Excel
    private static void exportToExcel(String filePath, List<ResultData> dataList) {
        EasyExcel.write(filePath, ResultData.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自动调整列宽
                .sheet("分析结果")
                .doWrite(dataList);
    }

    // 结果数据模型
    @Data
    public static class ResultData {

        @ExcelProperty("标签分类")
        private String tag;

        @ExcelProperty("总量")
        private Integer sheet2Count;

        @ExcelProperty("推荐")
        private Integer sheet3Count;

        @ExcelProperty("比例")
        private Double ratio;

        public ResultData(String tag, Integer sheet2Count, Integer sheet3Count, Double ratio) {
            this.tag = tag;
            this.sheet2Count = sheet2Count;
            this.sheet3Count = sheet3Count;
            this.ratio = ratio;
        }
    }
}