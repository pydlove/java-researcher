package com.aiocloud.test.excel.base;

import lombok.Data;

@Data
public class TrainMetadataGeneratorParam {

    private String trainFileName;
    private String testFileName;
    private Integer targetNum;
    private Boolean openFiledRuleAndAiSlice;
    private String metadataFileName;
    private String metadataFilePath;
}
