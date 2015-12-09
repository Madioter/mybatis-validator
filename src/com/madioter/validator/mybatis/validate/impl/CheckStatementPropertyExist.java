package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.InsertMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.SelectMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.sql.elementnode.ConditionNode;
import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
import com.madioter.validator.mybatis.model.sql.sqlnode.LimitNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.SelectNode;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfValueNode;
import com.madioter.validator.mybatis.model.sql.sqltag.ParameterMappingValidator;
import com.madioter.validator.mybatis.util.ArrayUtil;
import com.madioter.validator.mybatis.util.Config;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.mapping.ParameterMapping;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月04日 <br>
 */
public class CheckStatementPropertyExist extends AbstractValidator {

    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        StatementResource statementResource = configurationManager.getStatementResource();
        ColumnDao columnDao = connectionManager.getColumnDao();
        MappedStatementItem item = statementResource.getNext();
        while (item != null) {
            try {
                //中间增加一层动态代理类，通过传入Method 动态调用方法，并在其中增加过滤验证
                Method method = CheckStatementPropertyExist.this.getClass().getMethod("validatePropertyExist",
                        MappedStatementItem.class);
                getProxy().execute(method, item, columnDao);
                item = statementResource.getNext();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Validate property exist.
     * @author wangyi8
     * @taskId
     * @param item the item
     */
    public void validatePropertyExist(MappedStatementItem item) {
        if (item instanceof InsertMappedStatementItem) {
            validateInsertPropertyExist((InsertMappedStatementItem) item);
        } else if (item instanceof UpdateMappedStatementItem) {
            validateUpdatePropertyExist((UpdateMappedStatementItem) item);
        } else if (item instanceof SelectMappedStatementItem) {
            validateSelectPropertyExist((SelectMappedStatementItem) item);
        }
    }


    /**
     * Validate select property exist.
     * @author wangyi8
     * @taskId
     * @param item the item
     */
    private void validateSelectPropertyExist(SelectMappedStatementItem item) {
        List<SelectNode> selectNodeList = item.getSelectNodeList();
        for (int i = 0; i < selectNodeList.size(); i++) {
            List<SelectElement> elements = selectNodeList.get(i).selectElements();
            for (SelectElement element : elements) {
                if (element instanceof ConditionNode) {
                    checkConditionNodePropertyExist((ConditionNode) element, item.getParameterType(), item.getInfoMessage());
                } else if (element instanceof LimitNode) {
                    checkLimitNodePropertyExist((LimitNode) element, item.getParameterType(), item.getInfoMessage());
                }
            }
        }
    }


    /**
     * Check limit node property exist.
     * @author wangyi8
     * @taskId
     * @param element the element
     * @param clz the clz
     * @param errMsg the err msg
     */
    private void checkLimitNodePropertyExist(LimitNode element, Class clz, String errMsg) {
        checkPropertyExist(element.getStartParam(), clz, errMsg);
        if (element.getEndParam() != null) {
            checkPropertyExist(element.getEndParam(), clz, errMsg);
        }
    }


    /**
     * Check property exist.
     * @author wangyi8
     * @taskId
     * @param value the value
     * @param clz the clz
     * @param errMsg the err msg
     */
    private void checkPropertyExist(String value, Class clz, String errMsg) {
        if (value.contains(SymbolConstant.SYMBOL_LEFT_BRACE)) {
            if (clz == null || clz.equals(Map.class)) {
                return;
            } else {
                String[] str = value.split("\\#\\{");
                for (int i = 0; i < str.length; i++) {
                    if (str[i].contains(SymbolConstant.SYMBOL_RIGHT_BRACE)) {
                        String propertyName = str[i].substring(0, str[i].indexOf(SymbolConstant.SYMBOL_RIGHT_BRACE));
                        try {
                            ReflectHelper.haveGetMethod(propertyName, clz);
                        } catch (MapperException e) {
                            e.setDescription(errMsg + String.format(MessageConstant.EXPRESS_MSG, this.toString()) + e.getDescription());
                            e.printException();
                        }
                    }
                }
            }
        }
    }


    private void checkConditionNodePropertyExist(ConditionNode node, Class clz, String errMsg) {
        if (StringUtil.containBrace(node.getValue())) {
            if (clz == null || ArrayUtil.contains(Config.IGNORE_PARAMETER_TYPES, clz)) {
                return;
            } else {
                List<String> propertyNames = StringUtil.extractBrace(node.getValue());
                for (String propertyName : propertyNames) {
                    if (node.getConditionType().equals("in") && (propertyName.equals("item") || propertyName.startsWith("item."))) {
                        // foreach 条件在foreach标签中判断，这里不判断
                        return;
                    }
                    try {
                        ReflectHelper.haveGetMethod(propertyName, clz);
                    } catch (MapperException e) {
                        e.setDescription(errMsg + String.format(MessageConstant.EXPRESS_MSG, this.toString()) + e.getDescription());
                        e.printException();
                    }
                }
            }
        }
    }

    /**
     * Validate update property exist.
     * @author wangyi8
     * @taskId
     * @param item the item
     */
    private void validateUpdatePropertyExist(UpdateMappedStatementItem item) {
        List<ParameterMapping> parameterMappings = item.getParameterMappings();
        //验证属性是否存在
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMappingValidator validator = new ParameterMappingValidator(parameterMappings.get(i));
                try {
                    validator.validate();
                } catch (MapperException e) {
                    e.setDescription(item.getInfoMessage() + SymbolConstant.SYMBOL_COLON + e.getDescription());
                    e.printException();
                }
            }
        }
    }

    /**
     * Validate insert property exist.
     * @author wangyi8
     * @taskId
     * @param item the item
     */
    private void validateInsertPropertyExist(InsertMappedStatementItem item) {
        List<InsertIfValueNode> valueNodeList = item.getIfValueNodeList();
        Class parameterType = item.getParameterType();
        for (int i = 0; i < valueNodeList.size(); i++) {
            InsertIfValueNode valueNode = valueNodeList.get(i);
            try {
                valueNode.validate(parameterType);
            } catch (MapperException e) {
                e.setDescription(item.getInfoMessage() + SymbolConstant.SYMBOL_COLON + e.getDescription());
                e.printException();
            }
        }
    }
}
