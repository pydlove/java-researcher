package com.aiocloud.test.excel.main;

import com.aiocloud.test.excel.base.FieldInfo;
import com.aiocloud.test.excel.base.TrainMetadataGeneratorParam;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @description: FirstTrainMetadataGenerator.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-07-30 10:56 
 */
@Slf4j
public class PipelineGenerator {
//
//    public static final int SEQ = 9;
//    public static final int VERSION = 4;

//    public static final int SEQ = 3;
//    public static final int VERSION = 5;

    public static final int SEQ = 1;
    public static final int VERSION = 11;
    public static final String METADATA_SHEET_NAME = "字段核验信息";

    public static void main(String[] args) throws Exception {

        int targetNum = 500;

         firstStep(targetNum);
//
//        secondStep(targetNum);
//        secondStepV2(targetNum);
    }

    private static void secondStep(int targetNum) throws Exception {

        String sheetName = "智能分类测试结果明细";
        String originFileName = "ModelResultDetailFile (18)";
        PredictHandler.doPredict(originFileName, targetNum);
    }

    private static void secondStepV2(int targetNum) throws Exception {

        List<String> trainFileList = new ArrayList<>();
        trainFileList.add("（担保）训练集-V5-200-0.7-基础字段正则-1.xlsx");
        trainFileList.add("（担保）训练集-V5-200-0.7-基础字段正则-2.xlsx");
        trainFileList.add("（担保）训练集-V5-200-0.7-基础字段正则-3.xlsx");

        String sheetName = "智能分类测试结果明细";
        String originFileName = "ModelResultDetailFile (21)";
        PredictHandler.doPredictV2(originFileName, targetNum, trainFileList);
    }

    private static void firstStep(int targetNum) throws Exception {

        String metadataFileName = "0717优化（基础元数据）.xlsx";
        String metadataFilePath = CommonProcessor.BASE_PATH +
                "metadata" + File.separator + metadataFileName;

        TrainMetadataGeneratorParam trainMetadataGeneratorParam = new TrainMetadataGeneratorParam();
        trainMetadataGeneratorParam.setOpenFiledRuleAndAiSlice(true);
        trainMetadataGeneratorParam.setTargetNum(targetNum);
        trainMetadataGeneratorParam.setMetadataFileName(metadataFileName);
        trainMetadataGeneratorParam.setMetadataFilePath(metadataFilePath);

        // 找出包含基础字段的训练集
        String f1TrainPath = TrainMetadataGenerator.generate(trainMetadataGeneratorParam);

        // 分离测试集 f1TrainPath 中的基础字段和业务字段
        String keyName = metadataFileName.replace(".xlsx", "");
        String businessTestFileName = keyName + "_" + SEQ + ".xlsx";
        String businessTestFilePath = CommonProcessor.BASE_PATH +
                "train" + File.separator + "bussiness" + File.separator +
                "bussiness_" + businessTestFileName;

        FiledRuleAndAiSlice.doSlice(f1TrainPath, metadataFilePath, businessTestFilePath);

        // 从业务测试集得到 targetNum 的训练集
        String trainFileName = "（担保）训练集-V" + VERSION + "-" + targetNum + "-0.7-基础字段正则-" + SEQ + ".xlsx";
        String testFileName = "（担保）测试集-V" + VERSION + "-" + SEQ + ".xlsx";

        trainMetadataGeneratorParam.setOpenFiledRuleAndAiSlice(false);
        trainMetadataGeneratorParam.setTrainFileName(trainFileName);
        trainMetadataGeneratorParam.setTestFileName(testFileName);
        trainMetadataGeneratorParam.setMetadataFileName(businessTestFileName);
        trainMetadataGeneratorParam.setMetadataFilePath(businessTestFilePath);

        String f2TrainPath = TrainMetadataGenerator.generate(trainMetadataGeneratorParam);
    }

}
