package com.bowen.mybatis.reflection;

import com.bowen.mybatis.exception.ReflectionException;

import java.lang.reflect.Method;

public class BeanWrapper extends BaseWrapper {

  private final Object object;
  private Reflector reflector;


  public BeanWrapper(Object object) {
    this.object = object;
    this.reflector = Reflector.forClass(object.getClass());
  }

  @Override
  public Object getBeanProperty(String name) {
    Method method = reflector.getGetMethod(name);
    try {
      return method.invoke(object, NO_ARGUMENTS);
    } catch (Exception e) {
      throw new ReflectionException(e);
    }
  }

  @Override
  public String findProperty(String propertyName) {
    return  reflector.findPropertyName(propertyName);
  }

  @Override
  public void set(String property, Object value) {
    Method method = reflector.getSetMethod(property);
    Object[] params = {value};
    try {
      method.invoke(object,params);
    } catch (Exception e) {
      throw new ReflectionException("Could not set property '" + property + "' of '" + object.getClass() + "' with value '" + value + "' Cause: " + e.toString(), e);
    }
  }

  @Override
  public Class<?> getSetterType(String property) {
    return reflector.getSetterType(property);
  }

}
