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
    private  MyDataSource dataSource;
    private  Map<String, MappedStatement> mappedStatements = new HashMap<>();


    public  MappedStatement addMappedStatement(String id, MappedStatement mappedStatement) {
        return mappedStatements.put(id,mappedStatement);
    }


    public MappedStatement getMappedStatement(String statementId) {
        return mappedStatements.get(statementId);
    }
}
