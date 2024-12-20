package org.aiocloud.r2dbc;

import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import io.asyncer.r2dbc.mysql.MySqlResult;
import lombok.extern.slf4j.Slf4j;
import org.aiocloud.r2dbc.po.LoginUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @description:
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-19 13:45
 */
@Slf4j
public class R2DBCTest {

    public static void main(String[] args) throws IOException {

        MySqlConnectionConfiguration configuration = MySqlConnectionConfiguration.builder()
                .host("172.16.62.211")
                .port(3307)
                .username("root")
                .password("Abc@1234")
                .database("test1")
                .build();

        MySqlConnectionFactory factory = MySqlConnectionFactory.from(configuration);

        Flux<LoginUser> flux = factory.create()
                .flatMapMany(conn -> {
                    Flux<MySqlResult> resultFlux = conn.createStatement("select id, user_name from t_login_user where id = ?id and user_name = ?name")
                            .bind("id", 5L)
                            .bind("name", "sysadmin")
                            .execute();
                    return resultFlux;
                }).flatMap(result -> {
                    return result.map(rowMetadata -> {

                        Long id = rowMetadata.get("id", Long.class);
                        String userName = rowMetadata.get("user_name", String.class);
                        return new LoginUser(id, userName);
                    });
                });

        Mono.from(flux)
                .subscribe(result -> {
                    log.info("result name: {}", result.getUserName());
                });

        System.in.read();

    }
}
