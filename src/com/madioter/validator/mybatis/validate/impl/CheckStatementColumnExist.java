package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.InsertMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.SelectMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.database.TableDao;
import com.madioter.validator.mybatis.model.sql.elementnode.ConditionNode;
import com.madioter.validator.mybatis.model.sql.elementnode.QueryNode;
import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.ColumnNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.SelectNode;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfColumnNode;
import com.madioter.validator.mybatis.model.sql.sqltag.UpdateIfSetNode;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.SqlUtil;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月04日 <br>
 */
public class CheckStatementColumnExist extends AbstractValidator {


    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        StatementResource statementResource = configurationManager.getStatementResource();
        ColumnDao columnDao = connectionManager.getColumnDao();
        MappedStatementItem item = statementResource.getNext();
        while (item != null) {
            try {
                //中间增加一层动态代理类，通过传入Method 动态调用方法，并在其中增加过滤验证
                Method method = CheckStatementColumnExist.this.getClass().getMethod("validateColumnExist",
                        MappedStatementItem.class, ColumnDao.class);
                getProxy().execute(method, item, columnDao);
                item = statementResource.getNext();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Validate column exist.
     * @author wangyi8
     * @taskId
     * @param item the item
     * @param columnDao the column dao
     */
    public void validateColumnExist(MappedStatementItem item, ColumnDao columnDao) {
        if (item instanceof InsertMappedStatementItem) {
            validateInsertColumnExist((InsertMappedStatementItem) item, columnDao);
        } else if (item instanceof UpdateMappedStatementItem) {
            validateUpdateColumnExist((UpdateMappedStatementItem) item, columnDao);
        } else if (item instanceof SelectMappedStatementItem) {
            validateSelectColumnExist((SelectMappedStatementItem) item, columnDao);
        }
    }

    /**
     * Validate column exist.
     * @author wangyi8
     * @taskId
     * @param item the item
     * @param columnDao the column dao
     */
    public void validateSelectColumnExist(SelectMappedStatementItem item, ColumnDao columnDao) {
        Map<String, TableNode> aliasTable = new HashMap<String, TableNode>();
        List<SelectNode> selectNodeList = item.getSelectNodeList();
        if (selectNodeList != null) {
            for (int i = selectNodeList.size() - 1; i >= 0; i--) {
                SelectNode selectNode = selectNodeList.get(i);
                buildAliasTable(selectNode.getTableNodes(), aliasTable, item.getInfoMessage());
                List<SelectElement> selectElements = selectNode.selectElements();
                for (SelectElement element : selectElements) {
                    if (element instanceof QueryNode) {
                        ((QueryNode) element).get
                    }
                }
            }
        }
    }

    /**
     * 验证字段是否存在
     * @param columnName 字段名
     * @param aliasTable 表别称信息
     * @param columnDao 字段数据库比较dao
     * @param errMsg 异常信息
     */
    public void validateColumnExist(String columnName, Map<String, TableNode> aliasTable, ColumnDao columnDao, String errMsg) {
        if (SqlUtil.checkIsColumn(this.columnName)) {
            String[] strArr = columnName.split("\\" + SymbolConstant.SYMBOL_POINT);
            TableNode curTableNode = null;
            String curColumnName = null;
            if (strArr.length > 1) {
                curTableNode = aliasTable.get(strArr[0]);
                curColumnName = strArr[1];
            } else if (aliasTable.size() == 1) {
                Iterator<TableNode> tableNodeIterator = aliasTable.values().iterator();
                curTableNode = tableNodeIterator.next();
                curColumnName = strArr[0];
            } else if (aliasTable.containsKey(SelectMappedStatementItem.CURRENT_TABLE)) {
                curTableNode = aliasTable.get(SelectMappedStatementItem.CURRENT_TABLE);
                curColumnName = strArr[0];
            }
            if (curTableNode == null) {
                new MapperException(ExceptionCommonConstant.TABLE_ALIAS_IS_NULL,
                        errMsg + String.format(SQL_EXPRESS_TEXT, this.columnName)).printException();
            } else if (curTableNode.isCanCheck()) {
                boolean exist = columnDao.checkColumnExist(curColumnName, curTableNode.getTableName());
                if (!exist) {
                    new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                            errMsg + String.format(SQL_EXPRESS_TEXT, this.columnName)).printException();
                }
            }
        }
    }

    /**
     * 构建表别称Map
     * @param tableNodes 当前select节点数据表
     * @param aliasTable 表别称对象
     * @param errMsg 异常信息
     */
    private void buildAliasTable(List<TableNode> tableNodes, Map<String, TableNode> aliasTable, String errMsg) {
        if (tableNodes != null && tableNodes.size() == 1) {
            aliasTable.put(MessageConstant.CURRENT_TABLE, tableNodes.get(0));
        } else {
            aliasTable.remove(MessageConstant.CURRENT_TABLE);
        }
        for (TableNode tableNode : tableNodes) {
            if (tableNode.getTableAlias() != null && !tableNode.getTableAlias().equals("")) {
                if (tableNodes.size() > 1) {
                    new MapperException(ExceptionCommonConstant.TABLE_ALIAS_IS_NULL,
                            errMsg + String.format(MessageConstant.TABLE_NAME, tableNode.getTableName()));
                }
                aliasTable.put(tableNode.getTableAlias(), tableNode);
            } else {
                aliasTable.put(tableNode.getTableName().toLowerCase(), tableNode);
            }
        }
    }

    /**
     * Validate column exist.
     * @author wangyi8
     * @taskId
     * @param item the item
     * @param columnDao the column dao
     */
    public void validateUpdateColumnExist(UpdateMappedStatementItem item, ColumnDao columnDao) {
        List<TableNode> tableNodeList = item.getTableNodes();
        String tableName = "";
        if (tableNodeList != null && !tableNodeList.isEmpty()) {
            TableNode tableNode = tableNodeList.get(0);
            tableName = tableNode.getTableName() == null ? "" : tableNode.getTableName();
        }
        List<UpdateIfSetNode> setNodeList = item.getSetNodeList();
        if (StringUtil.isBlank(tableName)) {
            new MapperException(ExceptionCommonConstant.TABLE_NOT_EXIST, item.getInfoMessage() +
                    String.format(MessageConstant.TABLE_NAME, tableName)).printException();
            return;
        }
        if (setNodeList == null || setNodeList.isEmpty()) {
            return;
        }
        for (int i = 0; i < setNodeList.size(); i++) {
            UpdateIfSetNode node = setNodeList.get(i);
            //验证字段是否存在
            if (node.getIfContent() == null) {
                new MapperException(ExceptionCommonConstant.IF_TAG_EXPLAIN_ERROR, item.getInfoMessage() +
                        String.format(MessageConstant.EXPRESS_MSG, node.getContents()));
                continue;
            }
            if (!node.getIfContent().trim().endsWith(SymbolConstant.SYMBOL_COMMA)) {
                new MapperException(ExceptionCommonConstant.INSERT_END_WITH_COMMA, item.getInfoMessage() +
                        String.format(MessageConstant.EXPRESS_MSG, node.getIfContent()));
                continue;
            }
            String columnName = node.getIfContent().replace(SymbolConstant.SYMBOL_COMMA, "").trim();
            boolean result = columnDao.checkColumnExist(columnName, tableName);
            if (!result) {
                new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                        item.getInfoMessage() +
                                String.format(MessageConstant.TABLE_COLUMN_NAME, tableName, columnName)
                                + String.format(MessageConstant.EXPRESS_MSG, node.getIfContent()));
                continue;
            }
        }
    }

    /**
     * Validate column exist.
     * @author wangyi8
     * @taskId
     * @param item the item
     * @param columnDao the column dao
     */
    public void validateInsertColumnExist(InsertMappedStatementItem item, ColumnDao columnDao) {
        List<TableNode> tableNodeList = item.getTableNodes();
        String tableName = "";
        if (tableNodeList != null && !tableNodeList.isEmpty()) {
            TableNode tableNode = tableNodeList.get(0);
            tableName = tableNode.getTableName() == null ? "" : tableNode.getTableName();
        }
        List<InsertIfColumnNode> ifColumnNodes = item.getIfColumnNodeList();
        if (StringUtil.isBlank(tableName)) {
            new MapperException(ExceptionCommonConstant.TABLE_NOT_EXIST, item.getInfoMessage() +
                    String.format(MessageConstant.TABLE_NAME, tableName)).printException();
            return;
        }
        if (ifColumnNodes == null || ifColumnNodes.isEmpty()) {
            return;
        }
        for (int i = 0; i < ifColumnNodes.size(); i++) {
            InsertIfColumnNode columnNode = ifColumnNodes.get(i);
            if (columnNode.getIfContent() == null) {
                new MapperException(ExceptionCommonConstant.IF_TAG_EXPLAIN_ERROR, item.getInfoMessage() +
                        String.format(MessageConstant.EXPRESS_MSG, columnNode.getContents()));
                continue;
            }
            if (!columnNode.getIfContent().trim().endsWith(SymbolConstant.SYMBOL_COMMA)) {
                new MapperException(ExceptionCommonConstant.INSERT_END_WITH_COMMA, item.getInfoMessage() +
                        String.format(MessageConstant.EXPRESS_MSG, columnNode.getIfContent()));
                continue;
            }
            String columnName = columnNode.getIfContent().replace(SymbolConstant.SYMBOL_COMMA, "").trim();
            boolean result = columnDao.checkColumnExist(columnName, tableName);
            if (!result) {
                new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                        item.getInfoMessage() +
                                String.format(MessageConstant.TABLE_COLUMN_NAME, tableName, columnName)
                                + String.format(MessageConstant.EXPRESS_MSG, columnNode.getIfContent()));
                continue;
            }
        }
    }
}
