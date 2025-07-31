package com.aiocloud.test.license;

import com.aiocloud.test.license.po.LicenseManagement;
import com.aiocloud.test.license.mapper.LicenseManagementMapper;
import com.aiocloud.test.license.mapper.MetadataRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LicenseServiceImpl implements LicenseService {

    private final LicenseManagementMapper licenseMapper;
    private final MetadataRecordMapper metadataMapper;
    private final RedisDistributedLock distributedLock;

    @Override
    @Transactional
    public boolean tryAcquireLicense(int count) {

        // 获取分布式锁
        String lockKey = "license_acquire_lock";
        String requestId = UUID.randomUUID().toString();

        try {
            if (!distributedLock.tryLock(lockKey, requestId, 30)) {
                throw new RuntimeException("获取分布式锁失败");
            }

            LicenseManagement license = licenseMapper.selectForUpdate();
            if (license.getTotalLicense() - license.getUsedLicense() < count) {
                return false;
            }

            // 预扣减授权数
            licenseMapper.updateUsedLicense(license.getId(), license.getUsedLicense() + count, license.getVersion());
            return true;
        } finally {
            distributedLock.releaseLock(lockKey, requestId);
        }
    }

    @Override
    @Transactional
    public void confirmLicenseUsage(int count) {
        // 确认操作，可以记录日志等
        licenseMapper.confirmLicenseUsage(count);
    }

    @Override
    @Transactional
    public void rollbackLicense(int count) {
        LicenseManagement license = licenseMapper.selectForUpdate();
        licenseMapper.updateUsedLicense(license.getId(),
                license.getUsedLicense() - count, license.getVersion());
    }

    @Override
    @Transactional
    public int releaseLicenseForDatasource(long datasourceId) {
        String lockKey = "license_release_lock_" + datasourceId;
        String requestId = UUID.randomUUID().toString();

        try {
            if (!distributedLock.tryLock(lockKey, requestId, 30)) {
                throw new RuntimeException("获取分布式锁失败");
            }

            // 查询数据源下的元数据表数量
            int metadataCount = metadataMapper.countByDatasourceId(datasourceId);
            if (metadataCount > 0) {
                // 释放授权数
                LicenseManagement license = licenseMapper.selectForUpdate();
                licenseMapper.updateUsedLicense(license.getId(),
                        license.getUsedLicense() - metadataCount, license.getVersion());

                // 标记元数据为已删除
                metadataMapper.markAsDeletedByDatasourceId(datasourceId);
            }

            return metadataCount;
        } finally {
            distributedLock.releaseLock(lockKey, requestId);
        }
    }

    @Override
    public int getRemainingLicense() {
        return 0;
    }
}
