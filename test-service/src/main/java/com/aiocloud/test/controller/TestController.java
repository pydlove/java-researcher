package com.aiocloud.test.controller;

import com.aiocloud.test.dto.TestDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/get")
    public String get() {
        return "hello get";
    }

    @GetMapping("/get-name")
    public String getName(
            @RequestParam("name") String name
    ) {
        return "hello get: " + name;
    }

    @PostMapping("/post")
    public String post(
            @RequestBody String name
    ) {
        return "hello post: " + name;
    }

    @PostMapping("/post-dto")
    public String postDto(
            @RequestBody TestDTO testDTO
    ) {
        return "hello post dto: " + testDTO.toString();
    }

    @GetMapping("/exception")
    public String exception() {
        throw new RuntimeException("test");
    }

    @GetMapping("/white")
    public String white(
            @RequestParam("name") String name
    ) {
        return "hello white: " + name;
    }

    @GetMapping("/black")
    public String black(
            @RequestParam("name") String name
    ) {
        return "hello black: " + name;
    }
}
