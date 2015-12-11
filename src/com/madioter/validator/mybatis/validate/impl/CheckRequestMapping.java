package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.ResultMapResource;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.SelectMappedStatementItem;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.java.ClassModel;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月11日 <br>
 */
public class CheckRequestMapping extends AbstractValidator {
    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        ResultMapResource resultMapResource = configurationManager.getResultMapResource();
        Map<String, ClassModel> classModelCollection = resultMapResource.getClassModelCollection();
        Set<String> itemKeys = classModelCollection.keySet();

        for (String itemKey : itemKeys) {
            ClassModel item = classModelCollection.get(itemKey);
            try {
                //中间增加一层动态代理类，通过传入Method 动态调用方法，并在其中增加过滤验证
                Method method = CheckRequestMapping.this.getClass().getMethod("validateRequestMapping",
                        ClassModel.class);
                getProxy().execute(this, method, item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Validate request mapping.
     * @author wangyi8
     * @taskId
     * @param item the item
     */
    public void validateRequestMapping(ClassModel item) {
        item.validate();
    }
}
