package com.aiocloud.seata.xa.system.service.impl;

import com.aiocloud.seata.xa.account.service.AccountService;
import com.aiocloud.seata.xa.order.service.OrderService;
import com.aiocloud.seata.xa.system.service.BusinessService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {


    private final AccountService accountService;
    private final OrderService orderService;

    @GlobalTransactional
    @Override
    public void purchase(String userId, String commodityCode, int count, BigDecimal amount) {

        // 1. 扣减账户余额
        accountService.debit(userId, amount);

        // 2. 创建订单
        orderService.createOrder(userId, commodityCode, count, amount);

        // 模拟异常，测试分布式事务回滚
        if (count > 10) {
            throw new RuntimeException("购买数量超过限制");
        }
    }
}
