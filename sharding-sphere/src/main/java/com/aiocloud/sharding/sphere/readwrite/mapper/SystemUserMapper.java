package com.aiocloud.sharding.sphere.readwrite.mapper;

import com.aiocloud.sharding.sphere.readwrite.po.SystemUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SystemUserMapper {

    @Select("SELECT u FROM t_system_user u WHERE u.username = #{username}")
    SystemUser findByUsername(String username);

    @Select("SELECT * FROM t_system_user WHERE email = #{email}")
    List<SystemUser> selectByEmail(String email);

    @Insert("INSERT INTO t_system_user (username, password, email) VALUES (#{username}, #{password}, #{email})")
    Long save(SystemUser user);

    @Select("SELECT * FROM t_system_user WHERE id = #{id}")
    SystemUser selectById(Long id);
}
