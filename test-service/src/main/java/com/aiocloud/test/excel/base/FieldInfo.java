package com.aiocloud.test.excel.base;

import lombok.AllArgsConstructor;
import lombok.Data;

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
