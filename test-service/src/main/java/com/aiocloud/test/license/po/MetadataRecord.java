package com.aiocloud.test.license.po;

import lombok.Data;
import java.util.Date;

/**
 * 元数据记录实体类
 */
@Data
public class MetadataRecord {
    private Long id;
    private Long datasourceId;      // 数据源ID
    private String metadataName;    // 元数据表名
    private Integer status;         // 状态：1-有效，0-已删除
    private Date createdTime;
    private Date updatedTime;
}