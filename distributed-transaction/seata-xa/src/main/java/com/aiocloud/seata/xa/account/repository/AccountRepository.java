package com.aiocloud.seata.xa.account.repository;

import com.aiocloud.seata.xa.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUserId(String userId);

    @Modifying
    @Transactional
    @Query("UPDATE Account SET balance = balance - ?2 WHERE userId = ?1 AND balance >= ?2")
    int debit(String userId, BigDecimal money);

    @Modifying
    @Transactional
    @Query("UPDATE Account SET balance = balance + ?2 WHERE userId = ?1")
    int credit(String userId, BigDecimal money);
}