package org.aiocloud.r2dbc.service.impl;

import lombok.RequiredArgsConstructor;
import org.aiocloud.r2dbc.dao.LoginUserRepositories;
import org.aiocloud.r2dbc.po.LoginUser;
import org.aiocloud.r2dbc.service.LoginUserService;
import org.aiocloud.r2dbc.vo.LoginUserVO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-19 14:35
 */
@RequiredArgsConstructor
@Service
public class LoginUserServiceImpl implements LoginUserService {

    private final LoginUserRepositories loginUserRepositories;

    @Override
    public Flux<LoginUserVO> getUser(Long id) {

        Flux<LoginUserVO> flux = loginUserRepositories.findAllByIdAfter(id)
                .map(user -> {

                    LoginUserVO loginUserVO = new LoginUserVO();
                    loginUserVO.setId(user.getId());
                    loginUserVO.setUserName(user.getUserName());
                    loginUserVO.setTestName("test");

                    return loginUserVO;
                });

        return flux.delayElements(Duration.ofSeconds(1));
    }
}
