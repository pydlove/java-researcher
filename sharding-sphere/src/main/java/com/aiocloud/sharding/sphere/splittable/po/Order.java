package com.aiocloud.sharding.sphere.splittable.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String status;
}