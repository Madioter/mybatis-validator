package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.DeleteMappedStatementItem;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * <Description> 项目中不支持Delete语句 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月11日 <br>
 */
public class CheckDeleteStatementRefuse extends AbstractValidator {

    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        StatementResource statementResource = configurationManager.getStatementResource();
        Map<String, MappedStatementItem> itemMap = statementResource.getMappedStatementMap();
        Set<String> itemKeys = itemMap.keySet();

        for (String itemKey : itemKeys) {
            MappedStatementItem item = itemMap.get(itemKey);
            if (item instanceof DeleteMappedStatementItem) {
                try {
                    //中间增加一层动态代理类，通过传入Method 动态调用方法，并在其中增加过滤验证
                    Method method = CheckDeleteStatementRefuse.this.getClass().getMethod("refuseDeleteStatement",
                            DeleteMappedStatementItem.class);
                    getProxy().execute(this, method, item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 拒绝delete语句出现
     * @author wangyi8
     * @taskId
     * @param item the item
     */
    public void refuseDeleteStatement(DeleteMappedStatementItem item) {
        new MapperException(ExceptionCommonConstant.REFUSE_DELETE_STATEMENT, item.getInfoMessage()).printException();
    }
}
