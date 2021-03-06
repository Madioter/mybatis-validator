package com.madioter.validator.mybatis.model.java;

import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月12日 <br>
 */
public class ClassModel {

    /**
     * The Id.
     */
    private String id;

    /**
     * java类类型
     */
    private Class javaClass;

    /**
     * java类属性
     */
    private List<PropertyModel> properties = new ArrayList<PropertyModel>();

    /**
     * ResultMap信息构建
     * @param resultMap ResultMap
     */
    public ClassModel(ResultMap resultMap) {
        this.javaClass = resultMap.getType();
        this.id = resultMap.getId();
        List<ResultMapping> resultMappingList = resultMap.getResultMappings();
        for (ResultMapping resultMapping : resultMappingList) {
            properties.add(new PropertyModel(resultMapping));
        }
    }

    /**
     * 验证方法
     * @author wangyi8
     * @taskId
     */
    public void validate() {
        if (javaClass.equals(Map.class)) {
            new MapperException(ExceptionCommonConstant.MAP_PROPERTY_VALIDATE_ERROR, javaClass.getName()).printException();
        }
        for (PropertyModel propertyModel : properties) {
            try {
                propertyModel.validatePropertyExist(javaClass);
            } catch (MapperException e) {
                e.printException();
            }
        }
    }

    /**
     * Gets id.
     * @return the id
     */
    public String getId() {
        return id;
    }
}
