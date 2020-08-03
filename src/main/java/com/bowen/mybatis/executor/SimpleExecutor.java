package com.bowen.mybatis.executor;


import com.bowen.mybatis.DefaultParameterHandler;
import com.bowen.mybatis.DefaultResultSetHandler;
import com.bowen.mybatis.entity.Configuration;
import com.bowen.mybatis.entity.MappedStatement;
import com.bowen.mybatis.entity.MyDataSource;
import com.bowen.mybatis.parsing.GenericTokenParser;
import com.bowen.mybatis.parsing.VariableTokenHandler;
import com.bowen.mybatis.util.CommonUtis;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * mybatis执行器
 *
 * @author PLF
 * @date 2019年3月6日
 */
@Slf4j
public class SimpleExecutor implements Executor {
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
        if(params!=null){
            if(params.length>1){
                throw new RuntimeException("目前只支持一个参数，该参数可以为map");
            }
            if(params.length==1){
                parameterObject=params[0];
            }
        }


        //1.获取数据库连接
        Connection connection = getConnection();
        String originSql = mappedStatement.getSql();

        //myBatis用ongl取值,暂时不管
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
        DefaultParameterHandler parameterHandler = new DefaultParameterHandler(configuration, mappedStatement, parameterObject, prepareStatement);
        parameterHandler.setParameters();
        return prepareStatement;
    }


    @Override
    public  <E> List<E> selectList(MappedStatement mappedStatement, Object[] params) throws SQLException, ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        PreparedStatement preparedStatement = prepareSql(mappedStatement, params);

        //6.执行SQL，得到结果集ResultSet
        ResultSet resultSet = preparedStatement.executeQuery();

        DefaultResultSetHandler resultSetHandler = new DefaultResultSetHandler(resultSet, configuration, mappedStatement);
        return resultSetHandler.handleResultSets();
    }

    @Override
    public <T> T  selectOne(MappedStatement ms, Object[] args) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        List<T> results = this.selectList(ms, args);
        return CommonUtis.isNotEmpty(results) ? results.get(0) : null;
    }

    @Override
    public Object update(MappedStatement mappedStatement, Object[] params) throws SQLException {
        PreparedStatement preparedStatement = prepareSql(mappedStatement, params);
        //6.执行SQL，得到结果集ResultSet
        int result = preparedStatement.executeUpdate();
        return result;
    }
}
