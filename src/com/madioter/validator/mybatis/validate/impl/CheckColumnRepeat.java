package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.SelectMappedStatementItem;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.sql.elementnode.FunctionNode;
import com.madioter.validator.mybatis.model.sql.elementnode.QueryNode;
import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
import com.madioter.validator.mybatis.model.sql.sqlnode.SelectNode;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <Description>验证字段是否重复 <br>
 *     select 查询语句，不允许列名相同，例如：select a.id,b.id from table a ,table b
 * 解决方案：验证同一个语句的列名是否相同，优先别称，别称为空时，验证列名，出现相同时抛出错误信息
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月11日 <br>
 */
public class CheckColumnRepeat extends AbstractValidator {
    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        StatementResource statementResource = configurationManager.getStatementResource();
        Map<String, MappedStatementItem> itemMap = statementResource.getMappedStatementMap();
        Set<String> itemKeys = itemMap.keySet();

        for (String itemKey : itemKeys) {
            MappedStatementItem item = itemMap.get(itemKey);
            if (item instanceof SelectMappedStatementItem) {
                try {
                    //中间增加一层动态代理类，通过传入Method 动态调用方法，并在其中增加过滤验证
                    Method method = CheckColumnRepeat.this.getClass().getMethod("validateColumnRepeat",
                            SelectMappedStatementItem.class);
                    getProxy().execute(this, method, item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Validate column repeat.
     * @author wangyi8
     * @taskId
     * @param item the item
     */
    public void validateColumnRepeat(SelectMappedStatementItem item) {
        List<SelectNode> selectNodes = item.getSelectNodeList();
        for (SelectNode selectNode : selectNodes) {
            List<String> usedItemName = new ArrayList<String>();
            List<SelectElement> selectElements = selectNode.selectElements();

            for (SelectElement selectElement : selectElements) {
                String name = "";
                if (selectElement instanceof QueryNode) {
                    QueryNode queryNode = (QueryNode) selectElement;
                    queryNode.rebuild();
                    if (queryNode.getColumnAlias() != null) {
                        name = queryNode.getColumnAlias();
                    } else {
                        name = queryNode.getColumnName();
                    }
                    if (name.contains(SymbolConstant.SYMBOL_POINT)) {
                        String[] strArray = name.split(SymbolConstant.SYMBOL_SLASH + SymbolConstant.SYMBOL_POINT);
                        name = strArray[strArray.length - 1];
                    }
                } else if (selectElement instanceof FunctionNode) {
                    FunctionNode functionNode = (FunctionNode) selectElement;
                    if (functionNode.getAlias() != null) {
                        name = functionNode.getAlias();
                    } else {
                        name = functionNode.getExpress();
                    }
                }
                if (!StringUtil.isBlank(name) && usedItemName.contains(name)) {
                    new MapperException(ExceptionCommonConstant.COLUMN_NAME_REPEAT, item.getInfoMessage()
                            + String.format(MessageConstant.EXPRESS_MSG, selectElement.toString())).printException();
                } else {
                    usedItemName.add(name);
                }
            }
        }
    }
}
