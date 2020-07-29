package com.bowen.mybatis.reflection;

import com.bowen.mybatis.exception.ReflectionException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhaobowen
 * @create: 2020-07-28 20:03
 * @description:
 **/
@Slf4j
public class Reflector {

    private Class<?> cls;

    public Reflector(Class<?> type) {
        this.cls = type;
    }

    public Method getGetMethod(String propertyName) {
        Method[] methods = getClassMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.equals("get"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1))) {
                return method;
            }
        }
        throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + cls + "'");
    }

    /*
     * This method returns an array containing all methods
     * declared in this class and any superclass.
     * We use this method, instead of the simpler Class.getMethods(),
     * because we want to look for private methods as well.
     * 得到所有方法，包括private方法，包括父类方法.包括接口方法
     */
    private Method[] getClassMethods() {
        Map<String, Method> uniqueMethods = new HashMap<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());

            // we also need to look for interface methods -
            // because the class may be abstract
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                addUniqueMethods(uniqueMethods, anInterface.getMethods());
            }

            currentClass = currentClass.getSuperclass();
        }

        Collection<Method> methods = uniqueMethods.values();

        return methods.toArray(new Method[methods.size()]);
    }

    private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods) {
        for (Method currentMethod : methods) {
            if (!currentMethod.isBridge()) {
                //取得签名
                String signature = getSignature(currentMethod);
                log.debug("signature:{}",signature);
                // check to see if the method is already known
                // if it is known, then an extended class must have
                // overridden a method
                if (!uniqueMethods.containsKey(signature)) {
                    currentMethod.setAccessible(true);
                    uniqueMethods.put(signature, currentMethod);
                }
            }
        }
    }

    private String getSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        Class<?> returnType = method.getReturnType();
        if (returnType != null) {
            sb.append(returnType.getName()).append('#');
        }
        sb.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (i == 0) {
                sb.append(':');
            } else {
                sb.append(',');
            }
            sb.append(parameters[i].getName());
        }
        return sb.toString();
    }
}
