package com.aiocloud.twopc.controller;


import com.aiocloud.twopc.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/to")
    public String transfer(
            @RequestParam String fromAccount,
            @RequestParam String toAccount,
            @RequestParam double amount
    ) {
        try {
            transferService.transfer(fromAccount, toAccount, amount);
            return "Transfer successful";
        } catch (Exception e) {
            return "Transfer failed: " + e.getMessage();
        }
    }
}