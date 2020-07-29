package com.bowen.mybatis.annotation;

import com.bowen.mybatis.enums.JdbcType;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MappedJdbcTypes {
  JdbcType value();
}