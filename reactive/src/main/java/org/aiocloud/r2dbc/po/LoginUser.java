package org.aiocloud.r2dbc.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-19 14:41 
 */
@Table(value = "t_login_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser {

    private Long id;

    @Column(value = "user_name")
    private String userName;
}
