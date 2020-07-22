package com.bowen.mybatis;


import com.bowen.mybatis.constant.Constant;
import com.bowen.mybatis.entity.MappedStatement;
import com.bowen.mybatis.util.CommonUtis;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mybatis执行器
 *
 * @author PLF
 * @date 2019年3月6日
 */
public class SimpleExecutor {

    /**
     * 数据库连接
     */
    private static Connection connection;

    /**
     * #{}正则匹配
     */
    private static Pattern param_pattern = Pattern.compile("#\\{([^\\{\\}]*)\\}");

    public static Connection getConnection() {
        return connection;
    }

    static {
        initConnect();
    }

    private static void initConnect() {

//        String driver = ConfigFilesLoad.getProperty(Constant.DB_DRIVER_CONF);
        String url = ConfigFilesLoad.getProperty(Constant.DB_URL_CONF);
        String username = ConfigFilesLoad.getProperty(Constant.DB_USERNAME_CONF);
        String password = ConfigFilesLoad.getProperty(Constant.db_PASSWORD);

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("数据库连接成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  <E> List<E> selectList(MappedStatement mappedStatement, Object[] params) throws SQLException, ClassNotFoundException,
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

    public <T> T  selectOne(MappedStatement ms, Object[] args) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        List<T> results = this.selectList(ms, args);
        return CommonUtis.isNotEmpty(results) ? results.get(0) : null;
    }
}
