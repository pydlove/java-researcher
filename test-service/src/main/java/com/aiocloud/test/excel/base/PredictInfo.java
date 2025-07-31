package com.aiocloud.test.excel.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @description: PredictInfo.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-07-29 17:17 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictInfo {

    private String id;
    private String fieldName;
    private String fieldComment;
    private String fieldFeature;
    private String fieldMark;
    private String tableName;
    private String tableComment;
    private String tableMark;
    private String top1;
    private String top2;
    private String top3;
    private String top4;
    private String top5;
    private String correctTag;
    private String conclusion;
}
