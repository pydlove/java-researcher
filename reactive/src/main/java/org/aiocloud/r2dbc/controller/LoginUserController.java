package org.aiocloud.r2dbc.controller;

import lombok.RequiredArgsConstructor;
import org.aiocloud.r2dbc.service.LoginUserService;
import org.aiocloud.r2dbc.vo.LoginUserVO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @description:
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-19 14:34
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class LoginUserController {

    private final LoginUserService loginUserService;

    @GetMapping( value = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<LoginUserVO> getUser(@PathVariable("id") Long id) {
        return loginUserService.getUser(id);
    }
}
