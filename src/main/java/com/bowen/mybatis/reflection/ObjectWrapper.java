package com.bowen.mybatis.reflection;

public interface ObjectWrapper {

  Object getBeanProperty(String propName);

  String findProperty(String propertyName);

  void set(String property, Object value);

  Class<?> getSetterType(String property);
}
