package com.madioter.validator.mybatis.model.sql.sqltag;

import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import org.apache.ibatis.mapping.ParameterMapping;

/**
 * <Description> ParameterMapping验证类 <br>
 * 做出修改，作为ParameterMapping的装饰类
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年12月02日 <br>
 */
public class ParameterMappingValidator {
    /**
     * parameterMapping
     */
    private ParameterMapping parameterMapping;

    /**
     * Instantiates a new Parameter mapping validator.
     *
     * @param parameterMapping the parameter mapping
     */
    public ParameterMappingValidator(ParameterMapping parameterMapping) {
        this.parameterMapping = parameterMapping;
    }

    /**
     * Validate boolean.
     * @return boolean  @
     * @throws MapperException the mapper exception
     */
    public boolean validate() throws MapperException {
        //验证属性是否存在
        return ReflectHelper.haveGetMethod(parameterMapping.getProperty(), parameterMapping.getJavaType());
    }
}
