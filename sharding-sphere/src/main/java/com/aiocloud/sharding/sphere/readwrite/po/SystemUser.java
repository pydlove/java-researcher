package com.aiocloud.sharding.sphere.readwrite.po;

import lombok.Data;

@Data
public class SystemUser {

    private Long id;

    private String username;

    private String password;

    private String email;
}
