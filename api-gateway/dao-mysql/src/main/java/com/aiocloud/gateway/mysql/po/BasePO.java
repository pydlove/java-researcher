package com.aiocloud.gateway.mysql.po;

import lombok.Data;

import java.util.Date;

@Data
public class BasePO {

    private Long id;
    private Date updateTime;
    private Date createTime;
    private Integer deleteStatus;
    private Long modifyUid;
    private Long createUid;
}
