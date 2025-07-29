package com.aiocloud.test.excel.main;

import com.aiocloud.test.excel.base.FieldInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static void main(String[] args) throws Exception {

        BaseProcessor baseProcessor = new FiledRuleAndAiSlice();

        String trainFilePath = CommonProcessor.BASE_PATH +
                "metadata" + File.separator +
                "EFP_0717优化_test0721_1753670235464_1753752861647.xlsx";

        Map<String, FieldInfo> essentialFieldMap = new HashMap<>();

        List<FieldInfo> rows = CommonProcessor.readDatabaseMetadata(trainFilePath, baseProcessor.getSheetName());
        for (FieldInfo row : rows) {

            if ("否".equals(row.getEssentialField())) {
                continue;
            }

            essentialFieldMap.put(row.getFieldName(), row);
        }

        List<FieldInfo> businessFieldList = new ArrayList<>();

        int total = 0;
        int correctCount = 0;
        List<FieldInfo> fieldInfos = baseProcessor.readMetadata();
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

        // 写出业务字段信息
        // String outFilePath = CommonProcessor.BASE_PATH + "FRAAH_" + baseProcessor.keyName + "_" + total + "_" + correctCount + ".xlsx";
        // CommonProcessor.writeToExcel(businessFieldList, baseProcessor.sheetName, baseProcessor.getSheetName(), baseProcessor.inputFilePath, outFilePath);
        // log.info("writeToExcel finish, path: {}", outFilePath);

        BigDecimal accuracy = new BigDecimal(correctCount * 100.0 / total)
                .setScale(2, RoundingMode.HALF_UP);

        log.info("total: {}, correctCount: {}, accuracy: {}%", total, correctCount, accuracy);
    }
}
