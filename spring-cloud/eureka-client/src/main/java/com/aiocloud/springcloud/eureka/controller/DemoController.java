package com.aiocloud.springcloud.eureka.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Value("${server.port}")
    private String port;

    @RequestMapping("/hello")
    public String hello() {
        return "Hello from Eureka Client! Port: " + port;
    }
}
