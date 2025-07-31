package com.aiocloud.gateway.mysql.mapper;

import com.aiocloud.gateway.mysql.po.LoginUserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @description: LoginUserMapper.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-30 15:18
 */
@Repository
public interface LoginUserMapper {

    LoginUserPO findByUsername(@Param("username") String username);

    int deleteByPrimaryKey(Long id);

    int insert(LoginUserPO record);

    int insertSelective(LoginUserPO record);

    LoginUserPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoginUserPO record);

    int updateByPrimaryKey(LoginUserPO record);
}
