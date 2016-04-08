package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.DeleteMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.InsertMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.sql.elementnode.ConditionNode;
import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <Description> 检查子查询，insert，update，delete都不推荐使用子查询，防止死锁问题 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年04月06日 <br>
 */
public class CheckSubQuery extends AbstractValidator {

    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        StatementResource statementResource = configurationManager.getStatementResource();
        Map<String, MappedStatementItem> itemMap = statementResource.getMappedStatementMap();
        Set<String> itemKeys = itemMap.keySet();

        for (String itemKey : itemKeys) {
            MappedStatementItem item = itemMap.get(itemKey);
            if (item instanceof InsertMappedStatementItem) {
                try {
                    Method method = CheckSubQuery.this.getClass().getMethod("forbidInsertSubQuery",
                            InsertMappedStatementItem.class);
                    getProxy().execute(this, method, item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (item instanceof UpdateMappedStatementItem) {
                try {
                    Method method = CheckSubQuery.this.getClass().getMethod("forbidUpdateSubQuery",
                            UpdateMappedStatementItem.class);
                    getProxy().execute(this, method, item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 禁止insert语句中出现子查询
     * @author wangyi8
     * @taskId
     * @param item 语句
     */
    public void forbidInsertSubQuery(InsertMappedStatementItem item) {
        if (item.getInsertNode() != null && item.getInsertNode().getSqlNodeList() != null &&
                item.getInsertNode().getSqlNodeList().size() > 0) {
            new MapperException(ExceptionCommonConstant.FORBID_OPERATOR, item.getInfoMessage()).printException();
        }
    }

    /**
     * 禁止update语句中出现子查询
     * @author wangyi8
     * @taskId
     * @param item 语句
     */
    public void forbidUpdateSubQuery(UpdateMappedStatementItem item) {
        List<SelectElement> conditionNodeList = item.getWhereConditions();
        for (SelectElement element : conditionNodeList) {
            if (element instanceof ConditionNode) {
                if(element.toString().contains(SqlConstant.SELECT) && element.toString().contains(SqlConstant.FROM)) {
                    new MapperException(ExceptionCommonConstant.FORBID_OPERATOR, item.getInfoMessage()).printException();
                }
            }
        }
    }

}
