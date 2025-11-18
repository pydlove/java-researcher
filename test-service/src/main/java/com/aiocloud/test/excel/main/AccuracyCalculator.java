package com.aiocloud.test.excel.main;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class AccuracyCalculator {

    private static List<Double> scores = new ArrayList<>();

    public static void main(String[] args) {
        try {

            String targetFilePath = CommonProcessor.BASE_PATH +
                    "compare" + File.separator + "test11-3.xlsx";

            String metadataFilePath = CommonProcessor.BASE_PATH +
                    "compare" + File.separator + "正式训练集.xlsx";

            // 读取两个Excel文件
            FileInputStream v8File = new FileInputStream(targetFilePath);
            FileInputStream trainFile = new FileInputStream(metadataFilePath);

            Workbook v8Workbook = new XSSFWorkbook(v8File);
            Workbook trainWorkbook = new XSSFWorkbook(trainFile);

            // 获取工作表
            Sheet v8Sheet = v8Workbook.getSheet("字段核验信息");
            Sheet trainSheet = trainWorkbook.getSheet("数据集");

            // 解析数据
            List<Map<String, String>> v8Data = parseV8Data(v8Sheet);
            List<Map<String, String>> trainData = parseTrainData(trainSheet);

            double threshold = OptimalThresholdCalculator.calculateThreshold(scores, 95);

            // 计算指标
            calculateMetrics(v8Data, trainData, threshold);

            // 关闭资源
            v8Workbook.close();
            trainWorkbook.close();
            v8File.close();
            trainFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Map<String, String>> parseV8Data(Sheet sheet) {
        List<Map<String, String>> data = new ArrayList<>();
        Row headerRow = sheet.getRow(0);

        // 确定列索引
        int instanceNameIdx = -1;
        int tableNameIdx = -1;
        int fieldNameIdx = -1;
        int recTagIdx = -1;
        int refTagIdx = -1;
        int levelIdx = -1;

        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue();
            switch (header) {
                case "实例名称":
                    instanceNameIdx = cell.getColumnIndex();
                    break;
                case "表名称":
                    tableNameIdx = cell.getColumnIndex();
                    break;
                case "字段名称":
                    fieldNameIdx = cell.getColumnIndex();
                    break;
                case "推荐标签":
                    recTagIdx = cell.getColumnIndex();
                    break;
                case "参考标签":
                    refTagIdx = cell.getColumnIndex();
                    break;
                case "核验优先级(建议将3级和2级的结果核验完毕)":
                    levelIdx = cell.getColumnIndex();
                    break;
            }
        }

        // 解析数据行
        int level0 = 0;
        int level1 = 0;
        int level2 = 0;
        int level3 = 0;
        int emptyNum = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String levelVal = getCellValueAsString(row.getCell(levelIdx));
            if ("0.0".equals(levelVal)) {
                level0++;
                continue;
            }
            if ("1.0".equals(levelVal)) {
                level1++;
                if (StrUtil.isBlank(getCellValueAsString(row.getCell(refTagIdx)))) {
                    emptyNum++;
                }
            }
            if ("2.0".equals(levelVal)) {
                level2++;
                if (StrUtil.isBlank(getCellValueAsString(row.getCell(refTagIdx)))) {
                    emptyNum++;
                }
            }
            if ("3.0".equals(levelVal)) {
                level3++;
            }

            String refTag = getCellValueAsString(row.getCell(refTagIdx));
            if (StrUtil.isNotBlank(refTag)) {
                Double doubleScore = getDoubleScore(refTag);
                scores.add(doubleScore);
            }

            Map<String, String> record = new HashMap<>();
            record.put("instance", getCellValueAsString(row.getCell(instanceNameIdx)));
            record.put("table", getCellValueAsString(row.getCell(tableNameIdx)));
            record.put("field", getCellValueAsString(row.getCell(fieldNameIdx)));
            record.put("recTag", getCellValueAsString(row.getCell(recTagIdx)));
            record.put("refTag", getCellValueAsString(row.getCell(refTagIdx)));
            record.put("levelVal", levelVal);

            // 创建匹配键
            String matchKey = String.join("_",
                    record.get("instance"),
                    record.get("table"),
                    record.get("field"));
            record.put("matchKey", matchKey);

            data.add(record);
        }

        log.info("核验优先级 0: {}, 1: {}, 2: {}, 3: {}, 空白: {}", level0, level1, level2, level3, emptyNum);

        return data;
    }

    private static List<Map<String, String>> parseTrainData(Sheet sheet) {
        List<Map<String, String>> data = new ArrayList<>();
        Row headerRow = sheet.getRow(0);

        // 确定列索引
        int instanceIdx = -1;
        int tableNameIdx = -1;
        int fieldNameIdx = -1;
        int categoryIdx = -1;

        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue();
            switch (header) {
                case "实例":
                    instanceIdx = cell.getColumnIndex();
                    break;
                case "所属表名称":
                    tableNameIdx = cell.getColumnIndex();
                    break;
                case "字段名称":
                    fieldNameIdx = cell.getColumnIndex();
                    break;
                case "数据分类":
                    categoryIdx = cell.getColumnIndex();
                    break;
            }
        }

        // 解析数据行
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Map<String, String> record = new HashMap<>();
            record.put("instance", getCellValueAsString(row.getCell(instanceIdx)));
            record.put("table", getCellValueAsString(row.getCell(tableNameIdx)));
            record.put("field", getCellValueAsString(row.getCell(fieldNameIdx)));
            record.put("category", getCellValueAsString(row.getCell(categoryIdx)));

            // 创建匹配键
            String matchKey = String.join("_",
                    record.get("instance"),
                    record.get("table"),
                    record.get("field"));
            record.put("matchKey", matchKey);

            data.add(record);
        }
        return data;
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

    private static void calculateMetrics(List<Map<String, String>> v8Data,
                                         List<Map<String, String>> trainData, double threshold) {
        // 创建训练数据的查找表
        Map<String, String> trainLookup = new HashMap<>();
        for (Map<String, String> record : trainData) {
            trainLookup.put(record.get("matchKey"), record.get("category"));
        }

        int recCorrect = 0;
        int recTotal = 0;
        int top5Correct = 0;
        int top1Correct = 0;
        int matchedRecords = 0;
        int noMatchedRecords = 0;
        int emptyNum = 0;
        int newRecommendTotal = 0;
        int newRecommendRight = 0;
        int total = 0;
        int total1 = 0;

        for (Map<String, String> v8Record : v8Data) {

            String refTag = v8Record.get("refTag");
            String levelVal = v8Record.get("levelVal");
            if ("1.0".equals(levelVal)) {
                if (StrUtil.isBlank(refTag)) {
                    emptyNum++;
                }
                total1++;
            }
            if ("2.0".equals(levelVal)) {
                if (StrUtil.isBlank(refTag)) {
                    emptyNum++;
                }
            }

            String matchKey = v8Record.get("matchKey");
            String correctCategory = trainLookup.get(matchKey);
            if (refTag != null && !refTag.isEmpty()) {
                List<String> refLabels = parseReferenceLabels(refTag);
                Double doubleScore = getDoubleScore(refTag);
                if (doubleScore >=  threshold) {
                    newRecommendTotal++;
                    if (refLabels.get(0).equals(correctCategory)) {
                        newRecommendRight++;
                    }
                }
            }

            // 1. 计算推荐准确率
            String recTag = v8Record.get("recTag");
            if (recTag != null && !recTag.isEmpty()) {
                recTotal++;
                if (recTag.equals(correctCategory)) {
                    recCorrect++;
                }
                continue;
            }

            matchedRecords++;

            if (StrUtil.isBlank(refTag)) {
                continue;
            }

            if (correctCategory == null || correctCategory.isEmpty()) {
                log.info("未匹配的记录：{}", matchKey);
                continue;
            }

            if (refTag != null && !refTag.isEmpty()) {
                total++;
                List<String> refLabels = parseReferenceLabels(refTag);

                // TOP5检查
                boolean isTop5 = false;
                for (int i = 0; i < Math.min(5, refLabels.size()); i++) {
                    if (refLabels.get(i).equals(correctCategory)) {
                        top5Correct++;
                        isTop5 = true;
                        break;
                    }
                }

                if (!isTop5) {
                    noMatchedRecords++;
//                    log.info("未命中结果：{}", JSONObject.toJSONString(v8Record));
                }

                // TOP1检查
                if (!refLabels.isEmpty() && refLabels.get(0).equals(correctCategory)) {
                    top1Correct++;
                }
            }
        }

        // 输出结果
        log.info("匹配到的记录数: {}", matchedRecords);
        log.info("推荐准确率: {}/{} = {}%\n", recCorrect, recTotal, recTotal > 0 ? (recCorrect * 100.0 / recTotal) : 0);
        log.info("TOP5正确数: {}", top5Correct);
        log.info("TOP1正确数: {}", top1Correct);
        log.info("total: {}", total);
        log.info("total1: {}", total1);
        log.info("错误数: " + noMatchedRecords);
        log.info("总体信息: {}/{}/{}/{}", top5Correct + noMatchedRecords, recTotal, emptyNum, top5Correct + noMatchedRecords + recTotal + emptyNum);
        log.info("新推荐阈值: {} %", threshold);
        BigDecimal percentage = new BigDecimal(newRecommendRight * 100.0).divide(new BigDecimal(newRecommendTotal), 2, BigDecimal.ROUND_HALF_UP);

        log.info("新推荐信息: {}/{}, {} %", newRecommendRight, newRecommendTotal, percentage);
    }

    private static List<String> parseReferenceLabels(String refTagStr) {
        List<String> labels = new ArrayList<>();
        if (refTagStr == null || refTagStr.isEmpty()) {
            return labels;
        }

        // 分割标签
        String[] parts = refTagStr.split(",");
        Pattern pattern = Pattern.compile("\\([^)]*\\)");

        for (String part : parts) {
            // 去除百分比部分
            String cleaned = pattern.matcher(part).replaceAll("").trim();
            if (!cleaned.isEmpty()) {
                labels.add(cleaned);
            }
        }

        return labels;
    }

    private static Double getDoubleScore(String refTagStr) {
        List<String> labels = new ArrayList<>();
        if (refTagStr == null || refTagStr.isEmpty()) {
            return 0D;
        }

        // 分割标签
        String[] parts = refTagStr.split(",");
        Pattern pattern = Pattern.compile("\\([^)]*\\)");

        String substring = parts[0].substring(parts[0].indexOf("(") +1, parts[0].indexOf("%"));

        return Double.valueOf(substring);
    }
}