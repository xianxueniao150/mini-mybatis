package com.bowen.mybatis;

import com.bowen.mybatis.entity.Configuration;
import com.bowen.mybatis.entity.MappedStatement;
import com.bowen.mybatis.entity.ParameterMapping;
import com.bowen.mybatis.entity.TypeHandlerRegistry;
import com.bowen.mybatis.enums.JdbcType;
import com.bowen.mybatis.reflection.MetaObject;
import com.bowen.mybatis.typehandler.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author: zhaobowen
 * @create: 2020-07-29 20:08
 * @description:
 **/
public class DefaultParameterHandler {

    private Configuration configuration;
    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private PreparedStatement preparedStatement;

    public DefaultParameterHandler(Configuration configuration, MappedStatement mappedStatement, Object parameterObject, PreparedStatement preparedStatement) {
        this.configuration = configuration;
        this.mappedStatement = mappedStatement;
        this.parameterObject = parameterObject;
        this.preparedStatement = preparedStatement;
    }

    public void setParameters() throws SQLException {

        //5.目前只支持一个参数，该参数可以为字符串
        for (int j = 0; j < mappedStatement.getParameters().size(); j++) {
            ParameterMapping parameter = mappedStatement.getParameters().get(j);
            JdbcType jdbcType = parameter.getJdbcType();
            Object value = null;
            if(parameterObject!=null){
                String propertyName = parameter.getProperty();
                MetaObject metaObject =  new MetaObject(parameterObject);
                value = metaObject.getValue(propertyName);
            }
            if(value==null){
                jdbcType=JdbcType.VARCHAR;
            }
            TypeHandler handler = resolveTypeHandler(value, jdbcType);
            handler.setParameter(preparedStatement, j+1, value, jdbcType);
        }
    }

    private TypeHandler<?> resolveTypeHandler(Object value, JdbcType jdbcType) {
        TypeHandler<?> handler;
        if(value==null){
            handler= TypeHandlerRegistry.getObjectTypeHandler();
            return handler;
        }
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        handler = typeHandlerRegistry.getTypeHandler(value.getClass(), jdbcType);
        if (handler == null ) {
            handler = TypeHandlerRegistry.getObjectTypeHandler();
        }
        return handler;
    }
}
