package com.aiocloud.twopc.service.impl;

import com.aiocloud.twopc.entity.Account;
import com.aiocloud.twopc.repository.mysql.PrimaryAccountRepository;
import com.aiocloud.twopc.service.TransferService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @description: TransferServiceImpl.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-06-04 11:45 
 */
@RequiredArgsConstructor
@Service
public class TransferServiceImpl implements TransferService {

    private final PrimaryAccountRepository primaryAccountRepository;

    @PersistenceContext(unitName = "secondaryEntityManagerFactory")
    private EntityManager secondaryEntityManager;

    public void deductFromPrimary(String accountNumber, double amount) {

        // 从第一个数据源扣款
        Account fromAccount = primaryAccountRepository.findByAccountNumber(accountNumber);

        if (fromAccount == null) {
            throw new RuntimeException("From account not found");
        }

        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        primaryAccountRepository.save(fromAccount);
    }

    public void addToSecondary(String accountNumber, double amount) {

        // 向第二个数据源存款
        Account toAccount = secondaryEntityManager.createQuery(
                        "SELECT new com.aiocloud.twopc.entity.Account(a.id, a.accountName, a.accountNumber, a.balance) " +
                                "FROM Account a WHERE a.accountNumber = :accountNumber", Account.class)
                .setParameter("accountNumber", accountNumber)
                .getSingleResult();

        toAccount.setBalance(toAccount.getBalance() + amount);
        secondaryEntityManager.merge(toAccount);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transfer(String fromAccount, String toAccount, double amount) {

        // 使用JTA全局事务
        deductFromPrimary(fromAccount, amount);
        addToSecondary(toAccount, amount);
    }
}
