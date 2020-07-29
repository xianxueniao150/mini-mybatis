package com.bowen.mybatis.entity;

import com.bowen.mybatis.enums.JdbcType;
import com.bowen.mybatis.typehandler.TypeHandler;
import lombok.Data;

/**
 * @author: zhaobowen
 * @create: 2020-07-28 10:38
 * @description:
 **/
@Data
public class ParameterMapping {
    private String property;
    private Class<?> javaType = Object.class;
    private JdbcType jdbcType;
    private TypeHandler<?> typeHandler;

    public ParameterMapping(String property) {
        this.property = property;
    }
}
