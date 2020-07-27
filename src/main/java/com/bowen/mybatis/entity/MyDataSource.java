package com.bowen.mybatis.entity;

import lombok.Data;

/**
 * @author: zhaobowen
 * @create: 2020-07-27 10:57
 * @description:
 **/
@Data
public class MyDataSource {
    private String driver;
    private String url;
    private String username;
    private String password;
}
