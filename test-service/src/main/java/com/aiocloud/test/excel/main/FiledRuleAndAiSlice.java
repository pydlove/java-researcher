package com.aiocloud.test.excel.main;

import com.aiocloud.test.excel.base.FieldInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aiocloud.test.excel.main.PipelineGenerator.METADATA_SHEET_NAME;

/**
 *
 * @description: FiledRuleAndAiHandler.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-07-29 10:14 
 */
@Slf4j
public class FiledRuleAndAiSlice extends BaseProcessor {

    public static void doSlice(String trainFilePath, String metadataFilePath, String outFilePath) throws Exception {

        BaseProcessor baseProcessor = new FiledRuleAndAiSlice();
        boolean isWriteToExcel = true;
        int total = 0;
        int correctCount = 0;
        List<FieldInfo> businessFieldList = new ArrayList<>();

        Map<String, FieldInfo> essentialFieldMap = new HashMap<>();

        List<FieldInfo> rows = CommonProcessor.readDatabaseMetadata(trainFilePath, METADATA_SHEET_NAME);
        for (FieldInfo row : rows) {

            if ("否".equals(row.getEssentialField())) {
                continue;
            }

            log.info("essentialField, instanceName: {}, fieldName: {}, recommendedTag: {}",
                    row.getInstanceName(), row.getFieldName(), row.getRecommendedTag());

            essentialFieldMap.put(row.getFieldName(), row);
        }

        calculateEssentialFieldAccuracyRate(metadataFilePath, METADATA_SHEET_NAME, baseProcessor, businessFieldList, total, essentialFieldMap, correctCount);

        // 写出业务字段信息
        if (isWriteToExcel) {

            CommonProcessor.writeToExcel(
                    businessFieldList,
                    METADATA_SHEET_NAME,
                    METADATA_SHEET_NAME,
                    metadataFilePath,
                    outFilePath
            );

            log.info("writeToExcel finish, path: {}", outFilePath);
        }

    }

    private static void calculateEssentialFieldAccuracyRate(String metadataFilePath, String metadataSheetName, BaseProcessor baseProcessor, List<FieldInfo> businessFieldList, int total, Map<String, FieldInfo> essentialFieldMap, int correctCount) throws IOException {

        List<FieldInfo> fieldInfos = baseProcessor.readMetadata(metadataFilePath, metadataSheetName);
        for (FieldInfo fieldInfo : fieldInfos) {

            if ("否".equals(fieldInfo.getEssentialField())) {
                businessFieldList.add(fieldInfo);
                continue;
            }

            total++;
            String fieldName = fieldInfo.getFieldName();
            String correctTag = fieldInfo.getRecommendedTag();

            FieldInfo essentialFieldInfo = essentialFieldMap.get(fieldName);
            String recommendedTag = essentialFieldInfo.getRecommendedTag();
            if (recommendedTag.equals(correctTag)) {
                correctCount++;
            } else {
                log.info("Incorrect label, instanceName: {}, fieldName: {}, correctTag: {}, recommendedTag: {}",
                        fieldInfo.getInstanceName(), fieldName, correctTag, recommendedTag);
            }
        }

        BigDecimal accuracy = new BigDecimal(correctCount * 100.0 / total)
                .setScale(2, RoundingMode.HALF_UP);

        log.info("total: {}, correctCount: {}, accuracy: {}%", total, correctCount, accuracy);
    }
}
