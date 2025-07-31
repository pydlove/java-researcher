package com.aiocloud.seata.xa.account.service;

import com.aiocloud.seata.xa.account.entity.Account;

import java.math.BigDecimal;

public interface AccountService {

    void debit(String userId, BigDecimal money);

    void credit(String userId, BigDecimal money);

    Account getAccount(String userId);
}
