package com.aiocloud.test.license.mapper;

import com.aiocloud.test.license.po.LicenseManagement;
import org.apache.ibatis.annotations.*;

/**
 * 授权数管理Mapper接口
 */
@Mapper
public interface LicenseManagementMapper {

    /**
     * 查询授权信息（带悲观锁）
     */
    @Select("SELECT id, total_license as totalLicense, used_license as usedLicense, " +
            "version, create_time as createTime, update_time as updateTime " +
            "FROM license_management WHERE id = 1 FOR UPDATE")
    LicenseManagement selectForUpdate();

    /**
     * 更新已用授权数（带乐观锁）
     */
    @Update("UPDATE license_management SET " +
            "used_license = #{usedLicense}, " +
            "version = version + 1, " +
            "update_time = NOW() " +
            "WHERE id = #{id} AND version = #{version}")
    int updateUsedLicense(@Param("id") Long id,
                          @Param("usedLicense") Integer usedLicense,
                          @Param("version") Integer version);

    /**
     * 确认授权使用（直接更新）
     */
    @Update("UPDATE license_management SET " +
            "used_license = used_license + #{count}, " +
            "update_time = NOW() " +
            "WHERE id = 1")
    int confirmLicenseUsage(@Param("count") int count);

    /**
     * 获取当前授权信息（无锁）
     */
    @Select("SELECT id, total_license as totalLicense, used_license as usedLicense, " +
            "version, create_time as createTime, update_time as updateTime " +
            "FROM license_management WHERE id = 1")
    LicenseManagement selectCurrent();
}
