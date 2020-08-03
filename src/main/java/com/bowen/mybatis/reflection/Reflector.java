package com.bowen.mybatis.reflection;

import com.bowen.mybatis.exception.ReflectionException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: zhaobowen
 * @create: 2020-07-28 20:03
 * @description:
 **/
@Slf4j
public class Reflector {

    private Class<?> type;
    private static final Map<Class<?>, Reflector> REFLECTOR_MAP = new ConcurrentHashMap<>();
    private Map<String, String> caseInsensitivePropertyMap = new HashMap<>();
    private Map<String, Method> getMethods = new HashMap<>();
    //setter的方法列表
    private Map<String, Method> setMethods = new HashMap<>();
    private String[] readablePropertyNames;
    private Map<String, Class<?>> setTypes = new HashMap<>();



    /*
     * Gets an instance of ClassInfo for the specified class.
     * 得到某个类的反射器，是静态方法，而且要缓存，又要多线程，所以REFLECTOR_MAP是一个ConcurrentHashMap
     *
     * @param clazz The class for which to lookup the method cache.
     * @return The method cache for the class
     */
    public static Reflector forClass(Class<?> clazz) {
        // synchronized (clazz) removed see issue #461
        //对于每个类来说，我们假设它是不会变的，这样可以考虑将这个类的信息(构造函数，getter,setter,字段)加入缓存，以提高速度
        Reflector cached = REFLECTOR_MAP.get(clazz);
        if (cached == null) {
            cached = new Reflector(clazz);
            REFLECTOR_MAP.put(clazz, cached);
        }
        return cached;
    }

    private Reflector(Class<?> clazz) {
        this.type = clazz;
        //加入getter
        addGetMethods();
        //加入setter
        addSetMethods();
        readablePropertyNames = getMethods.keySet().toArray(new String[getMethods.keySet().size()]);
        for (String propName : readablePropertyNames) {
            //这里为了能找到某一个属性，就把他变成大写作为map的key。。。
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
    }

    private void addSetMethods() {
        Method[] methods = getClassMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("set") && name.length() > 3) {
                if (method.getParameterTypes().length == 1) {
                    name = PropertyNamer.methodToProperty(name);
                    setMethods.put(name,method);
                    setTypes.put(name, method.getParameterTypes()[0]);
                }
            }
        }
    }

    private void addGetMethods() {
        Method[] methods = getClassMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") && name.length() > 3) {
                if (method.getParameterTypes().length == 0) {
                    name = PropertyNamer.methodToProperty(name);
                    getMethods.put(name,method);
                }
            }
        }
    }

    public Method getGetMethod(String propertyName) {
        Method method = getMethods.get(propertyName);
        if (method == null) {
            throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
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
        Class<?> currentClass = type;
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


    public String findPropertyName(String name) {
        return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
    }

    public Method getSetMethod(String propertyName) {
        Method method = setMethods.get(propertyName);
        if (method == null) {
            throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
    }

    public Class<?> getSetterType(String propertyName) {
        Class<?> clazz = setTypes.get(propertyName);
        if (clazz == null) {
            throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return clazz;
    }
}
