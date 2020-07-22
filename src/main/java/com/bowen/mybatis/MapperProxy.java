package com.bowen.mybatis;

import com.bowen.mybatis.entity.MappedStatement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author: zhaobowen
 * @create: 2020-07-20 22:00
 * @description:
 **/
public class MapperProxy<T> implements InvocationHandler {

    private Class<T> target;

    private SimpleExecutor simpleExecutor;

    public  MapperProxy(Class<T> target) {
        this.target = target;
        this.simpleExecutor=new SimpleExecutor();
    }

    //返回代理对象
    @SuppressWarnings("unchecked")
    public  T getProxy() {

        /**
         * 参数一：被代理对象的类加载器
         * 参数二：被代理对象的接口
         * 参数三：InvocationHandler实现类
         */
        return (T) Proxy.newProxyInstance(target.getClassLoader(),
                new Class[] {this.target},
                this);
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
                    result = simpleExecutor.selectList(ms, args);
                } else {
//                    result = sqlSession.selectOne(statementId, args);
                    result = simpleExecutor.selectOne(ms, args);
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



}
