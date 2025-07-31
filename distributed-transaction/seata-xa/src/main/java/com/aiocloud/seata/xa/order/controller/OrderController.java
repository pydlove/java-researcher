package com.aiocloud.seata.xa.order.controller;

import com.aiocloud.seata.xa.system.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

    private final BusinessService businessService;

    @PostMapping("/purchase")
    public String purchase(
            @RequestParam String userId,
            @RequestParam String commodityCode,
            @RequestParam int count,
            @RequestParam BigDecimal amount) {

        businessService.purchase(userId, commodityCode, count, amount);
        return "下单成功";
    }
}