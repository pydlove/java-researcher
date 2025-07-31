package com.aiocloud.test.excel.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DatasetInfo {

    private String tableName;
    private String tableComment;
    private String fieldId;
    private String fieldName;
    private String fieldComment;
    private String recommendedTag;
}
