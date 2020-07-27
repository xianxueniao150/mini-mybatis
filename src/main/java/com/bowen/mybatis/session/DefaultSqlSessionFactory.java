package com.bowen.mybatis.session;

import com.bowen.mybatis.entity.Configuration;
import com.bowen.mybatis.xml.XMLConfigBuilder;

/**
 * @author: zhaobowen
 * @create: 2020-07-27 18:12
 * @description:
 **/
public class DefaultSqlSessionFactory {
    private Configuration configuration=new Configuration();

    public void loadConfig(String fileName) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(configuration);
        xmlConfigBuilder.buildConfig(fileName);
    }

    public DefaultSqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
