package com.aiocloud.test.license.po;

import lombok.Data;
import java.util.Date;

/**
 * 授权数管理实体类
 */
@Data
public class LicenseManagement {

    private Long id;

    // 总授权数
    private Integer totalLicense;

    // 已用授权数
    private Integer usedLicense;

    // 乐观锁版本号
    private Integer version;
    private Date createTime;
    private Date updateTime;

    /**
     * 获取剩余授权数
     */
    public Integer getRemainingLicense() {
        return totalLicense - usedLicense;
    }
}