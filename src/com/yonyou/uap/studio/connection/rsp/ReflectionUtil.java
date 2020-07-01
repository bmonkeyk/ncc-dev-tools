package com.yonyou.uap.studio.connection.rsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {
    protected static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class.getName());

    public static <T> T getProperty(Object obj, String property) {
        if (obj == null || property == null)
            return null;
        Class<? extends Object> clazz = obj.getClass();
        Field field = getDeclearedField(clazz, property);
        if (field == null)
            return null;
        String getter = "get" + property.substring(0, 1).toUpperCase() +
                property.substring(1);
        Object result = invokeGetter(obj, getter, field.getType());
        if (result != null)
            return (T) result;
        if (field.getType().equals(boolean.class) ||
                field.getType().equals(Boolean.class)) {
            getter = "is";
            if (property.startsWith("is")) {
                getter = String.valueOf(getter) + property.substring(2, 3).toUpperCase() +
                        property.substring(3);
            } else {
                getter = String.valueOf(getter) + property.substring(0, 1).toUpperCase() +
                        property.substring(1);
            }
            return (T) invokeGetter(obj, getter, field.getType());
        }
        return null;
    }

    public static boolean setProperty(Object obj, String property, Object value) {
        if (obj == null || property == null)
            return false;
        Class<? extends Object> clazz = obj.getClass();
        Field field = getDeclearedField(clazz, property);
        if (field == null)
            return false;
        String setterName = "set" + property.substring(0, 1).toUpperCase() +
                property.substring(1);
        if (invokeSetter(obj, setterName, field.getType(), value))
            return true;
        if (value instanceof Boolean && property.toLowerCase().startsWith("is") &&
                property.length() > 2) {
            setterName = "set" + property.substring(2, 3).toUpperCase() +
                    property.substring(3);
            return invokeSetter(obj, setterName, field.getType(), value);
        }
        return false;
    }

    private static Field getDeclearedField(Class<? extends Object> clazz, String property) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(property);
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
        }
        return field;
    }

    private static boolean invokeSetter(Object obj, String methodName, Class<?> clazz, Object value) {
        Method method = null;
        try {
            method = obj.getClass().getMethod(methodName, new Class[]{clazz});
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        }
        if (method != null)
            try {
                method.invoke(obj, new Object[]{value});
                return true;
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                logger.error(e.getMessage(), e);
            }
        return false;
    }

    private static Object invokeGetter(Object obj, String methodName, Class<?> clazz) {
        Method method = null;
        try {
            method = obj.getClass().getMethod(methodName, new Class[0]);
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        }
        if (method != null)
            try {
                return method.invoke(obj, new Object[0]);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                logger.error(e.getMessage(), e);
            }
        return null;
    }
}
