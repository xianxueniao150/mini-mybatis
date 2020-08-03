package com.bowen.mybatis;

import com.bowen.mybatis.entity.Configuration;
import com.bowen.mybatis.entity.MappedStatement;
import com.bowen.mybatis.entity.TypeHandlerRegistry;
import com.bowen.mybatis.reflection.MetaObject;
import com.bowen.mybatis.typehandler.TypeHandler;
import com.bowen.mybatis.util.ReflectionUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaobowen
 * @create: 2020-07-31 10:35
 * @description:
 **/
public class DefaultResultSetHandler {

    private final ResultSet resultSet;
    private final Configuration configuration;
    private final MappedStatement mappedStatement;
    private final TypeHandlerRegistry typeHandlerRegistry;

    public DefaultResultSetHandler(ResultSet resultSet, Configuration configuration, MappedStatement mappedStatement) {
        this.resultSet = resultSet;
        this.configuration = configuration;
        this.mappedStatement = mappedStatement;
        this.typeHandlerRegistry=configuration.getTypeHandlerRegistry();
    }

    public <E> List<E> handleResultSets() throws SQLException {
        final List<E> multipleResults = new ArrayList<>();
        String resultType = mappedStatement.getResultType();
        ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();
        while(resultSet.next()){
            Object resultObject = ReflectionUtil.newInstance(resultType);
            MetaObject metaObject = new MetaObject(resultObject);
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                final String property = metaObject.findProperty(columnName);
                if (property != null ) {
                    final Class<?> propertyType = metaObject.getSetterType(property);
                    final TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(propertyType);
                    //巧妙的用TypeHandler取得结果
                    final Object value = typeHandler.getResult(resultSet, columnName);
                    metaObject.setValue(property, value);
                }
            }
            multipleResults.add((E)resultObject);
        }

        return multipleResults;
    }

}
