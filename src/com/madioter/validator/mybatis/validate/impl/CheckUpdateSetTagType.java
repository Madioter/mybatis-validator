package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.sql.sqltag.UpdateIfSetNode;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public class CheckUpdateSetTagType extends AbstractValidator {

    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        StatementResource statementResource = configurationManager.getStatementResource();
        Map<String, MappedStatementItem> itemMap = statementResource.getMappedStatementMap();
        Set<String> itemKeys = itemMap.keySet();

        for (String itemKey : itemKeys) {
            MappedStatementItem item = itemMap.get(itemKey);
            if (item instanceof UpdateMappedStatementItem) {
                try {
                    Method method = CheckUpdateSetTagType.this.getClass().getMethod("validateIfTagType",
                            UpdateMappedStatementItem.class);
                    getProxy().execute(this, method, item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 验证属性是否存在
     * @author wangyi8
     * @taskId
     * @param item the item
     */
    public void validateIfTagType(UpdateMappedStatementItem item) {
        List<UpdateIfSetNode> setNodeList = item.getSetNodeList();
        if (setNodeList != null) {
            for (int i = 0; i < setNodeList.size(); i++) {
                UpdateIfSetNode node = setNodeList.get(i);
                if (node.getIfContent() == null) {
                    new MapperException(ExceptionCommonConstant.IF_TAG_EXPLAIN_ERROR, item.getInfoMessage() +
                            String.format(MessageConstant.EXPRESS_MSG, node.getContents())).printException();
                    continue;
                }
                if (!node.getIfContent().trim().endsWith(SymbolConstant.SYMBOL_COMMA)) {
                    new MapperException(ExceptionCommonConstant.UPDATE_END_WITH_COMMA, item.getInfoMessage()
                            + String.format(MessageConstant.EXPRESS_MSG, node.getIfContent())).printException();
                }
            }
        }
    }
}
