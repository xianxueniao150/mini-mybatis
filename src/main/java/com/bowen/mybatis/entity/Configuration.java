package com.bowen.mybatis.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhaobowen
 * @create: 2020-07-27 11:24
 * @description:
 **/
@Data
public class Configuration {
    private static MyDataSource dataSource;
    private static Map<String, MappedStatement> mappedStatements = new HashMap<>();


    public static void setDataSource(MyDataSource dataSource) {
        Configuration.dataSource = dataSource;
    }

    public static MyDataSource getDataSource() {
        return dataSource;
    }

    public static MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public static MappedStatement addMappedStatement(String id, MappedStatement mappedStatement) {
        return mappedStatements.put(id,mappedStatement);
    }
}
