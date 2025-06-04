package com.aiocloud.twopc.service;

/**
 *
 * @description: TransferService.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-06-04 11:46 
 */
public interface TransferService {

    void transfer(String fromAccountNumber, String toAccountNumber, double amount);
}
