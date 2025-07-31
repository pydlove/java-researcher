package org.aiocloud.r2dbc.dao;

import org.aiocloud.r2dbc.po.LoginUser;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-19 14:23
 */
@Repository
public interface LoginUserRepositories extends R2dbcRepository<LoginUser, Long> {

    Flux<LoginUser> findAllByIdAfter(Long id);
}
