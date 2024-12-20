package org.aiocloud.r2dbc.convert;

import io.r2dbc.spi.Row;
import org.aiocloud.r2dbc.vo.LoginUserVO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 *
 * @description:
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-19 14:26
 */
@ReadingConverter
public class CustomConverter implements Converter<Row, LoginUserVO> {

    @Override
    public LoginUserVO convert(Row source) {

        if(source == null) return null;

        LoginUserVO loginUserVO = new LoginUserVO();

        loginUserVO.setId(source.get("id", Long.class));
        loginUserVO.setUserName(source.get("user_name", String.class));
        loginUserVO.setTestName("test name");

        return loginUserVO;
    }

    @Override
    public <U> Converter<Row, U> andThen(Converter<? super LoginUserVO, ? extends U> after) {
        return Converter.super.andThen(after);
    }
}
