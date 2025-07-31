package com.aiocloud.test.license;

public interface LicenseService {

    /**
     * 尝试获取授权
     * @param count 需要的授权数量
     * @return 操作是否成功
     */
    boolean tryAcquireLicense(int count);

    /**
     * 确认授权使用
     * @param count 确认的授权数量
     */
    void confirmLicenseUsage(int count);

    /**
     * 回滚授权
     * @param count 回滚的授权数量
     */
    void rollbackLicense(int count);

    /**
     * 释放授权（删除数据源时调用）
     * @param datasourceId 数据源ID
     * @return 释放的授权数量
     */
    int releaseLicenseForDatasource(long datasourceId);

    /**
     * 获取剩余授权数
     */
    int getRemainingLicense();
}