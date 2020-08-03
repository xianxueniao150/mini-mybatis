package com.bowen.mybatis.reflection;

/**
 * @author: zhaobowen
 * @create: 2020-07-28 17:01
 * @description:
 **/
public class MetaObject {
    private final ObjectWrapper objectWrapper;

    public MetaObject(Object object) {
        this.objectWrapper = new BeanWrapper(object);
    /*    if (object instanceof Map) {
//            this.objectWrapper = new MapWrapper(this, (Map) object);
        } else if (object instanceof Collection) {
//            this.objectWrapper = new CollectionWrapper(this, (Collection) object);
        } else {
            this.objectWrapper = new BeanWrapper(this, object);
        }*/
    }

    public Object getValue(String name) {
        return objectWrapper.getBeanProperty(name);
    }

    public String findProperty(String propertyName) {
        return objectWrapper.findProperty(propertyName);
    }

    public void setValue(String property, Object value) {
        objectWrapper.set(property, value);
    }

    public Class<?> getSetterType(String property) {
        return objectWrapper.getSetterType(property);
    }
}
