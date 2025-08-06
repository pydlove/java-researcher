package com.aiocloud.test.excel.main;

import com.aiocloud.test.excel.base.DatasetInfo;
import com.aiocloud.test.excel.base.FieldInfo;
import com.aiocloud.test.excel.base.PredictInfo;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

import static com.aiocloud.test.excel.main.CommonProcessor.getStringValue;

/**
 *
 * @description: DatasetHandler.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-07-29 16:46 
 */
@Slf4j
public class DatasetHandler {

    public void writeToExcel(List<DatasetInfo> datasetInfos, String outputPath) throws Exception {

        String inputFilePath = CommonProcessor.BASE_PATH +
                "template" + File.separator + "训练集模版.xlsx";

        String sheetName = "数据集";

        try (FileInputStream fis = new FileInputStream(inputFilePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in the workbook");
            }

            List<String> headers = CommonProcessor.getHeaders(sheet);

            try (Workbook outputWorkbook = new XSSFWorkbook()) {

                Sheet outputSheet = outputWorkbook.createSheet(sheetName);

                // 写入表头
                CommonProcessor.writeHeader(outputSheet, headers);

                // 写入数据行
                writeDataRows(outputSheet, datasetInfos, headers);

                // 自动调整列宽
                CommonProcessor.autoSizeColumns(outputSheet, headers.size());

                // 保存文件
                try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                    outputWorkbook.write(fos);
                }
            }
        }
    }

    public void writeDataRows(Sheet sheet, List<DatasetInfo> datasetInfos, List<String> headers) {

        int rowNum = 1;
        for (DatasetInfo datasetInfo : datasetInfos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(datasetInfo.getFieldId());
            row.createCell(1).setCellValue(datasetInfo.getTableName());
            row.createCell(2).setCellValue(datasetInfo.getFieldComment());
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue("");
            row.createCell(5).setCellValue(datasetInfo.getTableName());
            row.createCell(6).setCellValue(datasetInfo.getTableComment());
            row.createCell(7).setCellValue("");
            row.createCell(8).setCellValue(datasetInfo.getRecommendedTag());
        }

        log.info("Create data rows complete: {}", rowNum - 1);
    }

    public static List<DatasetInfo> readDatasetList(String filePath, String sheetName) throws IOException {

        List<DatasetInfo> metadata = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in the workbook");
            }

            // 读取数据行
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 2; i <= lastRowNum; i++) {

                DatasetInfo datasetInfo = getDatasetInfo(sheet, i);
                if (datasetInfo == null) continue;

                if (i % 1000 == 0) {
                    log.info(" Read data for each column, index: {}/{}, column value: {}", i, lastRowNum, datasetInfo.getFieldName());
                }

                metadata.add(datasetInfo);
            }
        }

        return metadata;
    }

    public static DatasetInfo getDatasetInfo(Sheet sheet, int i) {

        Row row = sheet.getRow(i);
        if (row == null) return null;

        DatasetInfo predictInfo = new DatasetInfo();
        predictInfo.setFieldId(getStringValue(row.getCell(0)));
        predictInfo.setFieldName(getStringValue(row.getCell(1)));
        predictInfo.setFieldComment(getStringValue(row.getCell(2)));
        predictInfo.setTableName(getStringValue(row.getCell(5)));
        predictInfo.setTableComment(getStringValue(row.getCell(6)));
        predictInfo.setRecommendedTag(getStringValue(row.getCell(8)));

        return predictInfo;
    }
}
