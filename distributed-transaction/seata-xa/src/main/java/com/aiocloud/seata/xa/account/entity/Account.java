package com.aiocloud.seata.xa.account.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true)
    private String userId;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "freeze_amount")
    private BigDecimal freezeAmount;
}