package com.aiocloud.test.excel;

import com.aiocloud.test.excel.base.FieldInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 相似度分组统计
 * @description: ExcelSimilarityGrouping.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-07-22 14:30 
 */
@Slf4j
public class ExcelSimilarityGrouping extends BaseProcessor {

    public static void main(String[] args) throws IOException {

        BaseProcessor baseProcessor = new ExcelSimilarityGrouping();

        String outFilePath = CommonProcessor.BASE_PATH + "ESG_" + baseProcessor.getKeyName() + "_" + System.currentTimeMillis() + ".xlsx";
        baseProcessor.setOutFilePath(outFilePath);

        // 计算
        List<List<FieldInfo>> fixedGroups = baseProcessor.doProcess();

        // 创建新的Excel文件
        Workbook newWorkbook = new XSSFWorkbook();

        // 为每个分组创建一个sheet
        createSheetAndWrite(fixedGroups, newWorkbook, outFilePath);
    }

    private static void createSheetAndWrite(List<List<FieldInfo>> fixedGroups, Workbook newWorkbook, String outFilePath) throws IOException {

        int total = 0;
        for (int i = 0; i < fixedGroups.size(); i++) {

            List<FieldInfo> group = fixedGroups.get(i);
            Sheet sheet = newWorkbook.createSheet("Group_" + (i + 1) + "(" + group.size() + ")");
            log.info("create sheet finish, size: {}", group.size());
            total = total + group.size();

            List<String> headers = CommonProcessor.getHeaders(sheet);

            // 写入数据
            int rowNum = 1;
            for (FieldInfo fieldInfo : group) {
                CommonProcessor.writeDataRows(sheet, group, headers);
            }

            CommonProcessor.autoSizeColumns(sheet, 10);
        }

        // 保存新文件
        try (FileOutputStream out = new FileOutputStream(outFilePath)) {
            newWorkbook.write(out);
        }

        log.info("Excel file saved: {}, total: {}", outFilePath, total);
    }
}