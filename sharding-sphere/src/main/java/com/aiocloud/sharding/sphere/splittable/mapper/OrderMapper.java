package com.aiocloud.sharding.sphere.splittable.mapper;

import com.aiocloud.sharding.sphere.splittable.po.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("INSERT INTO t_order (order_id, user_id, amount, status) VALUES (#{orderId}, #{userId}, #{amount}, #{status})")
    void insert(Order order);

    @Select("SELECT * FROM t_order WHERE user_id = #{userId}")
    List<Order> selectByUserId(Long userId);
}
