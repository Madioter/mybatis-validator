package com.madioter.validator.mybatis.model;

import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.Map;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.type.JdbcType;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月12日 <br>
 */
public class PropertyModel {

    /**
     * 属性名
     */
    private String propertyName;

    /**
     * 对应字段名
     */
    private String columnName;

    /**
     * java类型
     */
    private Class javaType;

    /**
     * 数据库类型
     */
    private JdbcType jdbcType;

    /**
     * 构建属性信息
     * @param resultMapping ResultMapping
     */
    public PropertyModel(ResultMapping resultMapping) {
        this.columnName = resultMapping.getColumn();
        this.propertyName = resultMapping.getProperty();
        this.javaType = resultMapping.getJavaType();
        this.jdbcType = resultMapping.getJdbcType();
    }

    /**
     * 判断java端的属性是否存在，数据类型是否有效
     * @param clz 类定义
     * @throws MapperException 属性验证异常
     */
    public void validatePropertyExist(Class clz) throws MapperException {
        MapperException mapperException = null;
        try {
            ReflectHelper.haveGetMethod(propertyName, javaType, clz);
        } catch (MapperException e) {
            mapperException = e;
        }
        try {
            ReflectHelper.haveSetMethod(propertyName, javaType, clz);
        } catch (MapperException e) {
            mapperException.appendMessage(e.getMessage());
        }
        if (mapperException != null) {
            throw mapperException;
        }
    }
}
