package com.madioter.validator.mybatis.model.java;

import com.madioter.validator.mybatis.util.MyBatisTagConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
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
     * Instantiates a new Property model.
     *
     * @param propertyExpress the propery express
     */
    public PropertyModel(String propertyExpress) {
        if (propertyExpress.contains(SymbolConstant.SYMBOL_COMMA)) {
            String[] strArr = propertyExpress.split(SymbolConstant.SYMBOL_COMMA);
            this.propertyName = strArr[0];
            if (strArr.length > 1 && strArr[1].contains(MyBatisTagConstant.JDBC_TYPE)) {
                //TODO 需要将jdbcType转换为javaType才可以对参数属性类型做进一步验证
                String jdbcTypeStr = StringUtil.replaceBlank(strArr[1]).replace(MyBatisTagConstant.JDBC_TYPE + SymbolConstant.SYMBOL_EQUAL, "");
                for (JdbcType type : JdbcType.values()) {
                    if (type.toString().equals(jdbcTypeStr)) {
                        this.jdbcType = type;
                    }
                }
            }
        } else {
            this.propertyName = propertyExpress;
        }
    }

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

    /**
     * Gets property name.
     * @return the property name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Gets jdbc type.
     * @return the jdbc type
     */
    public JdbcType getJdbcType() {
        return jdbcType;
    }
}
