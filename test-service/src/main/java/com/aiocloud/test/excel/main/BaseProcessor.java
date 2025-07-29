package com.aiocloud.test.excel.main;

import com.aiocloud.test.excel.base.FieldInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Data
@Slf4j
public class BaseProcessor {

    // public String excelFileName = "（金融）全量训练数据测试0725_1753422342542.xlsx";
    // public String keyName = "（金融）全量训练数据测试0725_1753422342542";

//     public String excelFileName = "0717优化_test0721_1753670235464.xlsx";
//     public String keyName = "0717优化_test0721_1753670235464";

    public String excelFileName = "FRAAH_0717优化_test0721_1753670235464_4484_4114.xlsx";
    public String keyName = "FRAAH_0717优化_test0721_1753670235464_4484_4114";

    public String inputFilePath = CommonProcessor.BASE_PATH +
            "metadata" +
            File.separator + excelFileName;

    public String sheetName = "字段核验信息";
    public String nameField = "字段名称";
    public String commentField = "字段注释";
    public Boolean isOnlyFieldNameMatch = true;
    public double similarityThreshold = 0.7;
    public int targetNum = 1000;
    public int targetCount = targetNum / 10;
    public Boolean isPreDeduplication = true;
    public String outFilePath;

    public List<List<FieldInfo>> doProcess() throws IOException {

        List<FieldInfo> fieldInfos = readMetadata();

        // 按相似度分组
        List<List<FieldInfo>> similarityGroups = CommonProcessor.groupBySimilarity(fieldInfos, isOnlyFieldNameMatch, similarityThreshold);
        log.info("groupBySimilarity finish, size: {}", similarityGroups.size());

        // 大于平均数的就不参与合并
        int smallGroupThreshold = fieldInfos.size() / targetCount;

        // 去重
        // if (isPreDeduplication) {
        //     CommonProcessor.deduplicateGroups(similarityGroups, nameField, commentField);
        // }

        // 分组合并
        List<List<FieldInfo>> fixedGroups = CommonProcessor.forceMergeSmallGroups(similarityGroups, targetCount, smallGroupThreshold);
        log.info("Merged into {} fixed groups", fixedGroups.size());

        // 去重
        // CommonProcessor.deduplicateGroups(fixedGroups, nameField, commentField);

        // 排序
        fixedGroups.sort((g1, g2) -> Integer.compare(g2.size(), g1.size()));

        return fixedGroups;
    }

    public List<FieldInfo> readMetadata() throws IOException {

        // 读取原始数据
        List<FieldInfo> rows = CommonProcessor.readDatabaseMetadata(inputFilePath, sheetName);
        log.info("readExcelData finish, size: {}", rows.size());

        return rows;
    }
}
