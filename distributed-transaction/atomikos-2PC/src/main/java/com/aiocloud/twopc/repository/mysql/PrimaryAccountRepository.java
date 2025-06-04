package com.aiocloud.twopc.repository.mysql;

import com.aiocloud.twopc.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @description: AccountRepository.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-06-04 11:44 
 */
@Repository
public interface PrimaryAccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String accountNumber);
}