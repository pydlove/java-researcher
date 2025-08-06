package com.aiocloud.test.excel.base;

import org.apache.commons.text.similarity.CosineSimilarity;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @description: FieldInfoSimilarity.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-07-31 16:56 
 */
public class FieldInfoSimilarity {

    private static final JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
    private static final LevenshteinDistance distance = new LevenshteinDistance();

    public static double calculateTextSimilarity(String text1, String text2) {

        if (text1 == null || text2 == null) return 0.0;
        if (text1.isEmpty() && text2.isEmpty()) return 1.0;
        if (text1.isEmpty() || text2.isEmpty()) return 0.0;

        return jaccardSimilarity.apply(text1, text2);
    }

    /**
     * 计算两个FieldInfo对象的综合相似度
     * @param field1 第一个字段
     * @param field2 第二个字段
     * @return 相似度得分(0-1之间)
     */
    public static double calculateSimilarity(DatasetInfo field1, FieldInfo field2) {

        // 定义各属性的权重
        double nameWeight = 0.4;
        double tableWeight = 0.3;
        double commentWeight = 0.3;

        // 计算各属性的相似度
        double nameSimilarity = calculateTextSimilarity(
                field1.getFieldName(), field2.getFieldName());

        double tableSimilarity = calculateTextSimilarity(
                field1.getTableName(), field2.getTableName());

        double commentSimilarity = calculateTextSimilarity(
                field1.getFieldComment(), field2.getFieldComment());

        // 计算加权相似度
        return nameSimilarity * nameWeight +
                tableSimilarity * tableWeight +
                commentSimilarity * commentWeight;
    }

    public static double calculateSimilarityV1(DatasetInfo field1, FieldInfo field2) {

        String fieldName1 = field1.getFieldName();
        String fieldName2 = field2.getFieldName();

        // 计算相似度
        int maxLength = Math.max(fieldName1.length(), fieldName2.length());

        double similarity = 1 - (distance.apply(fieldName1, fieldName2) / (double) maxLength);

        return similarity;
    }


    public static List<FieldInfo> filterSimilarFields(
            List<DatasetInfo> trainFieldList,
            List<FieldInfo> hasResultFieldList,
            double threshold) {

        return hasResultFieldList.stream()
                .filter(testField ->
                        trainFieldList.stream()
                                .noneMatch(trainField ->
                                        FieldInfoSimilarity.calculateSimilarityV1(trainField, testField) > threshold
                                )
                )
                .collect(Collectors.toList());
    }

}