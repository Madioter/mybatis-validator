package com.madioter.validator.mybatis.util;

import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <Description> 反射帮助类 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月20日 <br>
 */
public class ReflectHelper {

    /**
     * 类属性不存在
     */
    private static final String CLASS_PROPERTY_EXIST = "%s.%s is not exist";

    /**
     * 通过属性名获取属性值
     * @param obj 对象
     * @param propertyName 属性名
     * @return 属性值
     * @throws ConfigException 配置异常
     */
    public static Object getPropertyValue(Object obj, String propertyName) throws ConfigException {
        Class clz = obj.getClass();
        while (!clz.equals(Object.class)) {
            try {
                Field field = clz.getDeclaredField(propertyName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
                clz = clz.getSuperclass();
                if (clz.equals(Object.class)) {
                    throw new ConfigException(ExceptionCommonConstant.MYBATIS_VERSION_EXCEPTION +
                            String.format(CLASS_PROPERTY_EXIST, clz.getName(), propertyName), e);
                }
            }
        }
        return null;
    }

    /**
     * 判断类是否有属性的get方法，不检查返回值类型
     *
     * @param propertyName 属性
     * @param clz 类定义
     * @return boolean 存在返回true
     * @throws MapperException get方法不存在
     */
    public static boolean haveGetMethod(String propertyName, Class clz) throws MapperException {
        Method method = null;
        try {
            method = clz.getMethod(StringUtil.getMethodName(propertyName));
        } catch (NoSuchMethodException e) {
            throw new MapperException(ExceptionCommonConstant.GET_METHOD_NOT_EXIST,
                    String.format(CLASS_PROPERTY_EXIST, clz.getName(), propertyName));
        }
        if (method != null) {
            return true;
        }
        return false;
    }
//com.tuniu.mauritius.ako.common.domain.CommonResourceInfo

    /**
     * 判断类是否有属性的get方法，检查返回值类型
     *
     * @param propertyName 属性
     * @param returnType 返回参数类型
     * @param clz 类定义
     * @return boolean 存在返回true
     * @throws MapperException get方法不存在
     */
    public static boolean haveGetMethod(String propertyName, Class returnType, Class clz) throws MapperException {
        MapperException mapperException = null;
        Method method = null;
        try {
            method = clz.getMethod(StringUtil.getMethodName(propertyName));
        } catch (NoSuchMethodException e) {
            mapperException = new MapperException(ExceptionCommonConstant.GET_METHOD_NOT_EXIST,
                    String.format(CLASS_PROPERTY_EXIST, clz.getName(), propertyName));
        }
        if (method != null) {
            if (!method.getReturnType().equals(returnType)) {
                mapperException = new MapperException(ExceptionCommonConstant.GET_METHOD_RETURN_TYPE,
                        String.format(CLASS_PROPERTY_EXIST, clz.getName(), propertyName));
            }
        }
        if (mapperException != null) {
            throw mapperException;
        }
        if (method != null) {
            return true;
        }
        return false;
    }

    /**
     * 判断类是否有属性的set方法
     *
     * @param propertyName 属性
     * @param paramType 参数类型
     * @param clz 类定义
     * @return boolean 存在返回true
     * @throws MapperException set方法不存在
     */
    public static boolean haveSetMethod(String propertyName, Class paramType, Class clz) throws MapperException {
        Method method = null;
        try {
            method = clz.getMethod(StringUtil.setMethodName(propertyName), paramType);
        } catch (NoSuchMethodException e) {
            throw new MapperException(ExceptionCommonConstant.SET_METHOD_NOT_EXIST,
                    String.format(CLASS_PROPERTY_EXIST, clz.getName(), propertyName));
        }
        if (method != null) {
            return true;
        }
        return false;
    }
}
