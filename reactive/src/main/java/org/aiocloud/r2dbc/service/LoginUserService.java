package org.aiocloud.r2dbc.service;

import org.aiocloud.r2dbc.vo.LoginUserVO;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LoginUserService {

    Flux<LoginUserVO> getUser(Long id);
}
