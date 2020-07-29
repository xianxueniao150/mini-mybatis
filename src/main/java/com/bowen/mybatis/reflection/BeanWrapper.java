package com.bowen.mybatis.reflection;

import com.bowen.mybatis.exception.ReflectionException;

import java.lang.reflect.Method;

public class BeanWrapper extends BaseWrapper {

  private final Object object;
  private Reflector reflector;


  public BeanWrapper(Object object) {
    this.object = object;
    this.reflector = new Reflector(object.getClass());
  }

  @Override
  public Object get(String name) {
    Method method = reflector.getGetMethod(name);
    try {
      return method.invoke(object, NO_ARGUMENTS);
    } catch (Exception e) {
      throw new ReflectionException(e);
    }
  }

}
