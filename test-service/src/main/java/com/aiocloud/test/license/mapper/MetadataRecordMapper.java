package com.aiocloud.test.license.mapper;

import com.aiocloud.test.license.po.MetadataRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 元数据记录Mapper接口
 */
@Mapper
public interface MetadataRecordMapper {

    /**
     * 插入元数据记录
     */
    @Insert("INSERT INTO metadata_records(" +
            "datasource_id, metadata_name, status, created_time, updated_time) " +
            "VALUES(#{datasourceId}, #{metadataName}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MetadataRecord record);

    /**
     * 根据数据源ID统计元数据表数量
     */
    @Select("SELECT COUNT(*) FROM metadata_records " +
            "WHERE datasource_id = #{datasourceId} AND status = 1")
    int countByDatasourceId(@Param("datasourceId") Long datasourceId);

    /**
     * 根据数据源ID标记为已删除
     */
    @Update("UPDATE metadata_records SET " +
            "status = 0, updated_time = NOW() " +
            "WHERE datasource_id = #{datasourceId} AND status = 1")
    int markAsDeletedByDatasourceId(@Param("datasourceId") Long datasourceId);

    /**
     * 根据ID查询元数据记录
     */
    @Select("SELECT id, datasource_id as datasourceId, metadata_name as metadataName, " +
            "status, created_time as createdTime, updated_time as updatedTime " +
            "FROM metadata_records WHERE id = #{id}")
    MetadataRecord selectById(@Param("id") Long id);

    /**
     * 批量插入元数据记录
     */
    @Insert("<script>" +
            "INSERT INTO metadata_records(datasource_id, metadata_name, status, created_time, updated_time) " +
            "VALUES " +
            "<foreach collection='records' item='record' separator=','>" +
            "(#{record.datasourceId}, #{record.metadataName}, 1, NOW(), NOW())" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("records") List<MetadataRecord> records);
}