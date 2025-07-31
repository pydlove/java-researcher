package com.aiocloud.gateway.center.system.loadbalance;

/**
 * @description: ServerLoadBalanceEnum.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-27 11:06
 */
public enum ServerLoadBalanceEnum {

    POLLING("polling", "轮询"),

    RANDOM("random", "随机"),

    WEIGHT("weight", "权重"),

    CONSISTENT_HASH("consistentHash", "一致性hash");

    private final String loadBalance;

    private final String desc;

    ServerLoadBalanceEnum(String loadBalance, String desc) {
        this.loadBalance = loadBalance;
        this.desc = desc;
    }

    public String getLoadBalance() {
        return loadBalance;
    }

    public static ServerLoadBalanceEnum getEnumByLoadBalance(String loadBalance) {

        for (ServerLoadBalanceEnum value : values()) {
            if (value.getLoadBalance().equals(loadBalance)) {
                return value;
            }
        }

        return null;
    }
}
