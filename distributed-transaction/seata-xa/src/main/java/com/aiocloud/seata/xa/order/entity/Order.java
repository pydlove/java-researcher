package com.aiocloud.seata.xa.order.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "commodity_code")
    private String commodityCode;

    @Column(name = "count")
    private Integer count;

    @Column(name = "amount")
    private BigDecimal amount;
}