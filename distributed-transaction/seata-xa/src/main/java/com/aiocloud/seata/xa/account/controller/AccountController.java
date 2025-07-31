package com.aiocloud.seata.xa.account.controller;

import com.aiocloud.seata.xa.account.entity.Account;
import com.aiocloud.seata.xa.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{userId}")
    public Account getAccount(@PathVariable String userId) {
        return accountService.getAccount(userId);
    }

    @PostMapping("/debit")
    public void debit(@RequestParam String userId, @RequestParam BigDecimal money) {
        accountService.debit(userId, money);
    }

    @PostMapping("/credit")
    public void credit(@RequestParam String userId, @RequestParam BigDecimal money) {
        accountService.credit(userId, money);
    }
}