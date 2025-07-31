package com.aiocloud.test.excel.main;

import com.aiocloud.test.excel.base.FieldInfo;
import com.aiocloud.test.excel.base.PredictInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aiocloud.test.excel.main.PipelineGenerator.SEQ;
import static com.aiocloud.test.excel.main.PipelineGenerator.VERSION;

/**
 *
 * @description: PredictHandler.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-07-29 17:20
 */
@Slf4j
public class PredictHandler extends BaseProcessor {

    @Getter
    public List<PredictInfo> noResultList;
    @Getter
    public List<PredictInfo> hasResultList;
    @Getter
    public int total;

    public static final String RESULT_SHEET_NAME = "智能分类测试结果明细";
    public static final int PREDICT_SEQ = SEQ + 1;

    public static void main(String[] args) throws Exception {

//        String lastTrainPath = CommonProcessor.BASE_PATH + "train" +
//                File.separator +
//                "EFP_FRAAH_0717优化_test0721_1753670235464_4484_4114_1753757321754.xlsx";
//
//        String sheetName = "智能分类测试结果明细";
//        String originFileName = "ModelResultDetailFile (4)";
//
//        ArrayList<String> lastTrainPaths = new ArrayList<>();
//        lastTrainPaths.add(lastTrainPath);
//
//        doPredict(lastTrainPaths, originFileName, sheetName);
    }

    public static void doPredict(String originFileName, int targetNum) throws Exception {

        String inputFilePath = CommonProcessor.BASE_PATH + "predict" +
                File.separator + originFileName + ".xlsx";

        PredictHandler predictHandler = new PredictHandler();
        predictHandler.init(inputFilePath, RESULT_SHEET_NAME);

        DatasetHandler datasetHandler = new DatasetHandler();

        List<FieldInfo> noResultFieldList = new ArrayList<>();
        for (PredictInfo predictInfo : predictHandler.getNoResultList()) {
            noResultFieldList.add(predictHandler.conver2FieldInfo(predictInfo));
        }

        List<FieldInfo> hasResultFieldList = new ArrayList<>();
        for (PredictInfo predictInfo : predictHandler.getHasResultList()) {
            hasResultFieldList.add(predictHandler.conver2FieldInfo(predictInfo));
        }

        int noResultFieldNum = noResultFieldList.size() * targetNum / predictHandler.getTotal();
        int hasResultFieldNum = targetNum - noResultFieldNum;
        log.info("noResultFieldList: {}, hasResultFieldList: {}", noResultFieldList.size(), hasResultFieldList.size());
        log.info("noResultFieldNum: {}, hasResultFieldNum: {}", noResultFieldNum, hasResultFieldNum);

        List<List<FieldInfo>> noResultGroupList = predictHandler.doGrouping(noResultFieldList, noResultFieldNum);
        List<FieldInfo> noResultSamplingFieldList = predictHandler.doSampling(noResultGroupList, noResultFieldNum);
        log.info("noResultSamplingFieldList finish, size: {}", noResultSamplingFieldList.size());

        List<List<FieldInfo>> hasResultGroupList = predictHandler.doGrouping(hasResultFieldList, hasResultFieldNum);
        List<FieldInfo> hasResultSamplingFieldList = predictHandler.doSampling(hasResultGroupList, hasResultFieldNum);
        log.info("hasResultSamplingFieldList finish, size: {}", hasResultSamplingFieldList.size());

        hasResultSamplingFieldList.addAll(noResultSamplingFieldList);

        // 测试集
        List<FieldInfo> allFieldList = new ArrayList<>();
        allFieldList.addAll(noResultFieldList);
        allFieldList.addAll(hasResultFieldList);

        Map<String, String> trainFieldMap = new HashMap<>();
        for (FieldInfo fieldInfo : hasResultSamplingFieldList) {
            trainFieldMap.put(fieldInfo.getFieldId(), fieldInfo.getFieldName());
        }

        List<FieldInfo> testFieldList = new ArrayList<>();
        for (FieldInfo fieldInfo : allFieldList) {
            if (!trainFieldMap.containsKey(fieldInfo.getFieldId())) {
                testFieldList.add(fieldInfo);
            }
        }

        String testFileName = "（担保）测试集-V" + VERSION + "-" + PREDICT_SEQ + ".xlsx";
        String testFilePath = CommonProcessor.BASE_PATH + "train" + File.separator + "test" + File.separator + testFileName;
        predictHandler.generateTrainFile(testFieldList, testFilePath);

        // 训练集
        String trainFileName = "（担保）训练集-V" + VERSION + "-" + targetNum + "-0.7-基础字段正则-" + PREDICT_SEQ + ".xlsx";
        String trainFilePath = CommonProcessor.BASE_PATH + "train" + File.separator + "target" + File.separator + trainFileName;
        predictHandler.generateTrainFile(hasResultSamplingFieldList, trainFilePath);
    }

    private FieldInfo conver2FieldInfo(PredictInfo predictInfo) {

        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setFieldId(predictInfo.getId());
        fieldInfo.setFieldName(predictInfo.getFieldName());
        fieldInfo.setFieldComment(predictInfo.getFieldComment());
        fieldInfo.setTableName(predictInfo.getTableName());
        fieldInfo.setTableComment(predictInfo.getTableComment());
        fieldInfo.setRecommendedTag(predictInfo.getCorrectTag());
        return fieldInfo;
    }

    public void init(String inputFilePath, String sheetName) throws IOException {

        noResultList = new ArrayList<>();
        hasResultList = new ArrayList<>();

        List<PredictInfo> predictInfos = readPredictMetadata(inputFilePath, sheetName);
        for (PredictInfo predictInfo : predictInfos) {
            if ("无结果".equals(predictInfo.getConclusion())) {
                noResultList.add(predictInfo);
            } else {
                hasResultList.add(predictInfo);
            }
        }

        total = predictInfos.size();
    }

    public List<PredictInfo> readPredictMetadata(String filePath, String sheetName) throws IOException {

        List<PredictInfo> metadata = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in the workbook");
            }

            // 读取数据行
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 2; i <= lastRowNum; i++) {

                PredictInfo predictInfo = getFieldMetadata(sheet, i);
                if (predictInfo == null) continue;

                if (i % 1000 == 0) {
                    log.info(" Read data for each column, index: {}/{}, column value: {}", i, lastRowNum, predictInfo.getFieldName());
                }

                metadata.add(predictInfo);
            }
        }

        return metadata;
    }

    public static PredictInfo getFieldMetadata(Sheet sheet, int i) {

        Row row = sheet.getRow(i);
        if (row == null) return null;

        PredictInfo predictInfo = new PredictInfo(
                String.valueOf(i),
                CommonProcessor.getStringValue(row.getCell(0)),
                CommonProcessor.getStringValue(row.getCell(1)),
                CommonProcessor.getStringValue(row.getCell(2)),
                CommonProcessor.getStringValue(row.getCell(3)),
                CommonProcessor.getStringValue(row.getCell(4)),
                CommonProcessor.getStringValue(row.getCell(5)),
                CommonProcessor.getStringValue(row.getCell(6)),
                CommonProcessor.getStringValue(row.getCell(7)),
                CommonProcessor.getStringValue(row.getCell(8)),
                CommonProcessor.getStringValue(row.getCell(9)),
                CommonProcessor.getStringValue(row.getCell(10)),
                CommonProcessor.getStringValue(row.getCell(11)),
                CommonProcessor.getStringValue(row.getCell(12)),
                CommonProcessor.getStringValue(row.getCell(13))
        );

        return predictInfo;
    }
}
