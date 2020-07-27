package com.bowen.mybatis.session;

import com.bowen.mybatis.MapperProxy;
import com.bowen.mybatis.entity.Configuration;

/**
 * @author: zhaobowen
 * @create: 2020-07-27 18:24
 * @description:
 **/
public class DefaultSqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration=configuration;
    }

    public <T> T getMapper(Class<T> type) {
        MapperProxy<T> mapperProxy = new MapperProxy<>(type,configuration);
        return mapperProxy.getProxy();
    }
}
