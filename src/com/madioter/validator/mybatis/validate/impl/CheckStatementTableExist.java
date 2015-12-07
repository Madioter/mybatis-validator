package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.database.TableDao;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <Description> 验证sql表达式中的表是否存在 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月04日 <br>
 */
public class CheckStatementTableExist extends AbstractValidator {

    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        StatementResource statementResource = configurationManager.getStatementResource();
        TableDao tableDao = connectionManager.getTableDao();
        MappedStatementItem item = statementResource.getNext();
        while (item != null) {
            try {
                //中间增加一层动态代理类，通过传入Method 动态调用方法，并在其中增加过滤验证
                Method method = CheckStatementTableExist.this.getClass().getMethod("validateTableExist",
                        MappedStatementItem.class, TableDao.class);
                getProxy().execute(method, item, tableDao);
                item = statementResource.getNext();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 验证单条sql语句中的表是否存在
     * @param item 单条sql语句信息
     * @param tableDao 数据表操作DAO
     */
    public void validateTableExist(MappedStatementItem item, TableDao tableDao) {
        List<TableNode> tableNodes = item.getTableNodes();
        for (int i = 0; i < tableNodes.size(); i++) {
            String tableName = tableNodes.get(i).getTableName();
            boolean exist = tableDao.checkExist(tableName);
            if (!exist) {
                new MapperException(ExceptionCommonConstant.TABLE_NOT_EXIST,
                        item.getInfoMessage() + String.format(MessageConstant.TABLE_NAME, tableName)).printException();
            }
        }

    }
}
