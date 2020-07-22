package com.bowen.mybatis.enums;

/**
 * @author: zhaobowen
 * @create: 2020-07-22 19:40
 * @description:
 **/
public enum SqlType {
    SELECT("select"),
    INSERT("insert"),
    UPDATE("update"),
    DEFAULT("default");

    private String value;

    SqlType(String value)
    {
        this.value = value;
    }

    public String value()
    {
        return this.value;
    }
}
