package com.bowen.mybatis;


import com.bowen.mybatis.entity.*;
import com.bowen.mybatis.enums.JdbcType;
import com.bowen.mybatis.parsing.GenericTokenParser;
import com.bowen.mybatis.parsing.VariableTokenHandler;
import com.bowen.mybatis.reflection.MetaObject;
import com.bowen.mybatis.typehandler.TypeHandler;
import com.bowen.mybatis.util.CommonUtis;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * mybatis执行器
 *
 * @author PLF
 * @date 2019年3月6日
 */
@Slf4j
public class SimpleExecutor {
    private Configuration configuration;

    /**
     * 数据库连接
     */
    private  Connection connection;

    public SimpleExecutor(Configuration configuration) {
        this.configuration = configuration;
    }


    public  Connection getConnection() {
        MyDataSource dataSource = configuration.getDataSource();
//        String driver = ConfigFilesLoad.getProperty(Constant.DB_DRIVER_CONF);
        String url = dataSource.getUrl();
        String username = dataSource.getUsername();
        String password = dataSource.getPassword();
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("数据库连接成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }


    public PreparedStatement prepareSql(MappedStatement mappedStatement, Object[] params) throws SQLException {
        Object parameterObject=null;
        if(params.length>1){
            throw new RuntimeException("目前只支持一个参数，该参数可以为map");
        }
        if(params.length==1){
            parameterObject=params[0];
        }

        //1.获取数据库连接
        Connection connection = getConnection();
        String originSql = mappedStatement.getSql();
        if(parameterObject!=null){
            if ((Map.class).isAssignableFrom(parameterObject.getClass())) {
                Map paramMap = (Map) parameterObject;
                VariableTokenHandler tokenHandler = new VariableTokenHandler(paramMap);
                GenericTokenParser tokenParser = new GenericTokenParser("${", "}", tokenHandler);
                originSql = tokenParser.parse(originSql);
                log.debug(originSql);
            }
        }

        PreparedStatement prepareStatement = connection.prepareStatement(originSql);


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
            handler.setParameter(prepareStatement, j+1, value, jdbcType);
        }
      return prepareStatement;
    }

    private TypeHandler<?> resolveTypeHandler(Object value, JdbcType jdbcType) {
        TypeHandler<?> handler;
        if(value==null){
            handler=TypeHandlerRegistry.getObjectTypeHandler();
            return handler;
        }
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        handler = typeHandlerRegistry.getTypeHandler(value.getClass(), jdbcType);
        if (handler == null ) {
            handler = TypeHandlerRegistry.getObjectTypeHandler();
        }
        return handler;
    }

    public  <E> List<E> selectList(MappedStatement mappedStatement, Object[] params) throws SQLException, ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        PreparedStatement preparedStatement = prepareSql(mappedStatement, params);

        //6.执行SQL，得到结果集ResultSet
        ResultSet resultSet = preparedStatement.executeQuery();

        //7.实例化ResultSetHandler，通过反射将ResultSet中结果设置到目标resultType对象中
        List<E> result = new ArrayList<>();

        if (null == resultSet) {
            return null;
        }

        while (resultSet.next()) {
            // 通过反射实例化返回类
            Class<?> entityClass = Class.forName(mappedStatement.getResultType());
            @SuppressWarnings("unchecked")
            E entity = (E) entityClass.newInstance();
            Field[] declaredFields = entityClass.getDeclaredFields();

            for (Field field : declaredFields) {
                // 对成员变量赋值
                field.setAccessible(true);
                Class<?> fieldType = field.getType();

                // 目前只实现了string和int转换
                if (String.class.equals(fieldType)) {
                    field.set(entity, resultSet.getString(field.getName()));
                } else if (int.class.equals(fieldType) || Integer.class.equals(fieldType)) {
                    field.set(entity, resultSet.getInt(field.getName()));
                } else {
                    // 其他类型自己转换，这里就直接设置了
                    field.set(entity, resultSet.getObject(field.getName()));
                }
            }

            result.add(entity);
        }

        return result;
    }

    public <T> T  selectOne(MappedStatement ms, Object[] args) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        List<T> results = this.selectList(ms, args);
        return CommonUtis.isNotEmpty(results) ? results.get(0) : null;
    }

    public Object update(MappedStatement mappedStatement, Object[] params) throws SQLException {
        PreparedStatement preparedStatement = prepareSql(mappedStatement, params);
        //6.执行SQL，得到结果集ResultSet
        int result = preparedStatement.executeUpdate();
        return result;
    }
}
