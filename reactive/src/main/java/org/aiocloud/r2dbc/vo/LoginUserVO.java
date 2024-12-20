package org.aiocloud.r2dbc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aiocloud.r2dbc.po.LoginUser;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-19 14:27
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserVO extends LoginUser {

    private String testName;
}
