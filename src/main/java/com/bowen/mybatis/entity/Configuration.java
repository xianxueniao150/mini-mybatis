package com.bowen.mybatis.entity;

import com.bowen.mybatis.executor.Executor;
import com.bowen.mybatis.executor.SimpleExecutor;
import com.bowen.mybatis.plugin.Interceptor;
import com.bowen.mybatis.plugin.InterceptorChain;
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
    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
    protected final InterceptorChain interceptorChain = new InterceptorChain();


    public  MappedStatement addMappedStatement(String id, MappedStatement mappedStatement) {

        return mappedStatements.put(id,mappedStatement);
    }
    public void addInterceptor(Interceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
    }

    public MappedStatement getMappedStatement(String statementId) {
        return mappedStatements.get(statementId);
    }

    public Executor newExecutor() {
        Executor executor = new SimpleExecutor(this);
        executor = (Executor) interceptorChain.pluginAll(executor);
        return executor;
    }
}
