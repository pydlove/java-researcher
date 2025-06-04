package com.aiocloud.sharding.sphere.splittable.controller;

import com.aiocloud.sharding.sphere.splittable.mapper.OrderMapper;
import com.aiocloud.sharding.sphere.splittable.po.Order;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderMapper orderMapper;

    @PostMapping("/create")
    public String createOrder(@RequestParam Long userId, @RequestParam Long orderId) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setAmount(new BigDecimal("100.00"));
        order.setStatus("CREATED");
        orderMapper.insert(order);
        return "Order created: " + orderId;
    }

    @GetMapping("/list")
    public List<Order> listOrders(@RequestParam Long userId) {
        return orderMapper.selectByUserId(userId);
    }
}