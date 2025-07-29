package com.aiocloud.seata.xa.order.service;


import com.aiocloud.seata.xa.order.entity.Order;

import java.math.BigDecimal;

public interface OrderService {

    Order createOrder(String userId, String commodityCode, int count, BigDecimal amount);
}
