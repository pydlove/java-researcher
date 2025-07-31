package com.aiocloud.seata.xa.account.service.impl;

import com.aiocloud.seata.xa.account.entity.Account;
import com.aiocloud.seata.xa.account.repository.AccountRepository;
import com.aiocloud.seata.xa.account.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    @Override
    public void debit(String userId, BigDecimal money) {
        int affected = accountRepository.debit(userId, money);
        if (affected == 0) {
            throw new RuntimeException("扣款失败，余额不足");
        }
    }

    @Transactional
    @Override
    public void credit(String userId, BigDecimal money) {
        accountRepository.credit(userId, money);
    }

    @Override
    public Account getAccount(String userId) {
        return accountRepository.findByUserId(userId);
    }
}