package com.aiocloud.seata.xa.system.service;

import java.math.BigDecimal;

public interface BusinessService {

    void purchase(String userId, String commodityCode, int count, BigDecimal amount);
}
