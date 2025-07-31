package com.aiocloud.test.excel.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @description: FieldInfo.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-07-29 17:34
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FieldInfo {
    private String assetName;
    private String instanceName;
    private String tableId;
    private String tableName;
    private String tableComment;
    private String fieldId;
    private String fieldName;
    private String fieldComment;
    private String recommendedTag;
    private String essentialField;

}
