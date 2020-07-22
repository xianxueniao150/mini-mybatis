package com.bowen.mybatis;

import com.bowen.mybatis.constant.Constant;
import com.bowen.mybatis.entity.MappedStatement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: zhaobowen
 * @create: 2020-07-20 22:00
 * @description:
 **/
public class MapperProxy implements InvocationHandler {

    private Object target;

    public MapperProxy(Object target) {
        this.target = target;
    }

    //返回代理对象
    @SuppressWarnings("unchecked")
    public <T> T getProxy() {

        /**
         * 参数一：被代理对象的类加载器
         * 参数二：被代理对象的接口
         * 参数三：InvocationHandler实现类
         */
        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                this);
    }

    /**
     * #{}正则匹配
     */
    private static Pattern param_pattern = Pattern.compile("#\\{([^\\{\\}]*)\\}");

    /**
     * 数据库连接
     */
    private static Connection connection;

    static {
        initConnect();
    }
    private static void initConnect() {

        String driver = ConfigFilesLoad.getProperty(Constant.DB_DRIVER_CONF);
        String url = ConfigFilesLoad.getProperty(Constant.DB_URL_CONF);
        String username = ConfigFilesLoad.getProperty(Constant.DB_USERNAME_CONF);
        String password = ConfigFilesLoad.getProperty(Constant.db_PASSWORD);

        try {
//            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("数据库连接成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //如果时Object类中的方法，直接执行
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        return this.execute(method, args);
    }

    /**
     * 根据SQL指令执行对应操作
     *
     * @param method
     * @param args
     * @return
     */
    private Object execute(Method method, Object[] args) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        String statementId = method.getDeclaringClass().getName() + "." + method.getName();
        MappedStatement ms = ConfigFilesLoad.getMappedStatement(statementId);

        Object result = null;
        switch (ms.getSqlCommandType()) {
            case SELECT: {
                Class<?> returnType = method.getReturnType();

                // 如果返回的是list，应该调用查询多个结果的方法，否则只要查单条记录
                if (Collection.class.isAssignableFrom(returnType)) {
                    //ID为mapper类全名+方法名
                    result = selectList(ms, args);
                } else {
//                    result = sqlSession.selectOne(statementId, args);
                    result = selectList(ms, args);
                }
                break;
            }
            case UPDATE: {
                break;
            }
            default: {
                //TODO 其他方法待实现
                break;
            }
        }

        return result;
    }

    private <E> List<E> selectList(MappedStatement mappedStatement, Object[] params) throws SQLException, ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        //1.获取数据库连接
        Connection connection = getConnection();

        String originalSql = mappedStatement.getSql();
        originalSql = originalSql.trim();
        Matcher matcher = param_pattern.matcher(originalSql);
        String newSql = matcher.replaceAll("?");
        PreparedStatement prepareStatement = connection.prepareStatement(newSql);

        //5.实例化ParameterHandler，将SQL语句中？参数化
        if(params!=null){
            for (int i = 0; i < params.length; i++) {
                //Mapper保证传入参数类型匹配，这里就不做类型转换了
                prepareStatement.setObject(i + 1, params[i]);
            }
        }


        //6.执行SQL，得到结果集ResultSet
        ResultSet resultSet = prepareStatement.executeQuery();

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

}
