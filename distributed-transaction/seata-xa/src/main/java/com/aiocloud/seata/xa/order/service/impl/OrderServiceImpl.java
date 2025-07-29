package com.aiocloud.seata.xa.order.service.impl;

import com.aiocloud.seata.xa.order.entity.Order;
import com.aiocloud.seata.xa.order.repository.OrderRepository;
import com.aiocloud.seata.xa.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Order createOrder(String userId, String commodityCode, int count, BigDecimal amount) {
        Order order = new Order();
        order.setUserId(userId);
        order.setCommodityCode(commodityCode);
        order.setCount(count);
        order.setAmount(amount);
        return orderRepository.save(order);
    }
}
