package com.aiocloud.seata.xa.order.repository;

import com.aiocloud.seata.xa.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
