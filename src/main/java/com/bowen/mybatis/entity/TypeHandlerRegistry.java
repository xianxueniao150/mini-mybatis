package com.bowen.mybatis.entity;

import com.bowen.mybatis.annotation.MappedJdbcTypes;
import com.bowen.mybatis.annotation.MappedTypes;
import com.bowen.mybatis.enums.JdbcType;
import com.bowen.mybatis.exception.TypeException;
import com.bowen.mybatis.typehandler.ObjectTypeHandler;
import com.bowen.mybatis.typehandler.TypeHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: zhaobowen
 * @create: 2020-07-27 20:53
 * @description:
 **/
public class TypeHandlerRegistry {
    private static final ObjectTypeHandler OBJECT_TYPE_HANDLER = new ObjectTypeHandler();
    private final Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = new ConcurrentHashMap<>();

    public static ObjectTypeHandler getObjectTypeHandler() {
        return OBJECT_TYPE_HANDLER;
    }

    public void register(Class<?> typeHandlerClass) {
        MappedTypes mappedTypes = typeHandlerClass.getAnnotation(MappedTypes.class);
        MappedJdbcTypes mappedJdbcTypes = typeHandlerClass.getAnnotation(MappedJdbcTypes.class);
        Class<?> javaTypeClass=null;
        JdbcType jdbcType=null;
        if (mappedTypes != null) {
            javaTypeClass = mappedTypes.value();
        }
        if(mappedJdbcTypes != null){
            jdbcType = mappedJdbcTypes.value();
        }

        Map<JdbcType, TypeHandler<?>> map = typeHandlerMap.get(javaTypeClass);
        if (map == null ) {
            map = new HashMap<>();
            typeHandlerMap.put(javaTypeClass, map);
        }
//            TypeHandler<Object> instance = getInstance(typeHandlerClass);
        map.put(jdbcType, getInstance(typeHandlerClass));
    }

    @SuppressWarnings("unchecked")
    private  <T> TypeHandler<T> getInstance(Class<?> typeHandlerClass) {
        try {
            Constructor<?> c = typeHandlerClass.getConstructor();
            return (TypeHandler<T>) c.newInstance();
        } catch (Exception e) {
            throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
        }
    }

    public  <T> TypeHandler<T> getTypeHandler(Type type) {
        return getTypeHandler(type,null);
    }

    @SuppressWarnings("unchecked")
    public  <T> TypeHandler<T> getTypeHandler(Type type, JdbcType jdbcType) {
        Map<JdbcType, TypeHandler<?>> jdbcHandlerMap = typeHandlerMap.get(type);
        TypeHandler<?> handler = null;
        if (jdbcHandlerMap != null) {
            if(jdbcType!=null){
                handler = jdbcHandlerMap.get(jdbcType);
                if (handler == null) {
                    throw new RuntimeException("异常");
                }
            }else{
                handler = pickSoleHandler(jdbcHandlerMap);
            }
        }else{
            handler=OBJECT_TYPE_HANDLER;
        }
        // type drives generics here
        return (TypeHandler<T>) handler;
    }

    private TypeHandler<?> pickSoleHandler(Map<JdbcType, TypeHandler<?>> jdbcHandlerMap) {
        TypeHandler<?> soleHandler = null;
        for (TypeHandler<?> handler : jdbcHandlerMap.values()) {
            if (soleHandler == null) {
                soleHandler = handler;
            } else if (!handler.getClass().equals(soleHandler.getClass())) {
                // More than one type handlers registered.
                return null;
            }
        }
        return soleHandler;
    }
}
