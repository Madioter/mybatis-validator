package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.InsertMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.SelectMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.sql.elementnode.ConditionNode;
import com.madioter.validator.mybatis.model.sql.elementnode.FunctionNode;
import com.madioter.validator.mybatis.model.sql.elementnode.OrderNode;
import com.madioter.validator.mybatis.model.sql.elementnode.QueryNode;
import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.ColumnNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.GroupByNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.InsertNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.SelectNode;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfColumnNode;
import com.madioter.validator.mybatis.model.sql.sqltag.UpdateIfSetNode;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.SqlConstant;
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
import java.util.Set;

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
        Map<String, MappedStatementItem> itemMap = statementResource.getMappedStatementMap();
        Set<String> itemKeys = itemMap.keySet();

        for (String itemKey : itemKeys) {
            MappedStatementItem item = itemMap.get(itemKey);
            try {
                //中间增加一层动态代理类，通过传入Method 动态调用方法，并在其中增加过滤验证
                Method method = CheckStatementColumnExist.this.getClass().getMethod("validateColumnExist",
                        MappedStatementItem.class, ColumnDao.class);
                getProxy().execute(this, method, item, columnDao);
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
        String errMsg = item.getInfoMessage();
        if (selectNodeList != null) {
            for (int i = selectNodeList.size() - 1; i >= 0; i--) {
                SelectNode selectNode = selectNodeList.get(i);
                buildAliasTable(selectNode.getTableNodes(), aliasTable, item.getInfoMessage());
                List<SelectElement> selectElements = selectNode.selectElements();
                for (SelectElement element : selectElements) {
                    if (element != null) {
                        element.rebuild();
                        if (element instanceof QueryNode) {
                            checkQueryNodeColumnExist(aliasTable, (QueryNode) element, columnDao, errMsg);
                        } else if (element instanceof FunctionNode) {
                            checkFunctionNodeColumnExist(aliasTable, (FunctionNode) element, columnDao, errMsg);
                        } else if (element instanceof ConditionNode) {
                            checkConditionNodeColumnExist(aliasTable, (ConditionNode) element, columnDao, errMsg);
                        } else if (element instanceof OrderNode) {
                            checkOrderNodeColumnExist(aliasTable, (OrderNode) element, columnDao, errMsg);
                        } else if (element instanceof GroupByNode) {
                            checkGroupNodeColumnExist(aliasTable, (OrderNode) element, columnDao, errMsg);
                        }
                    }
                }
            }
        }
    }


    /**
     * Check group node column exist.
     * @author wangyi8
     * @taskId
     * @param aliasTable the alias table
     * @param element the element
     * @param columnDao the column dao
     * @param errMsg the err msg
     */
    public void checkGroupNodeColumnExist(Map<String, TableNode> aliasTable, OrderNode element, ColumnDao columnDao, String errMsg) {
        return;
    }

    /**
     * Check order node column exist.
     * @author wangyi8
     * @taskId
     * @param aliasTable the alias table
     * @param element the element
     * @param columnDao the column dao
     * @param errMsg the err msg
     */
    public void checkOrderNodeColumnExist(Map<String, TableNode> aliasTable, OrderNode element, ColumnDao columnDao, String errMsg) {
        String column = element.getOrderColumn();
        if (StringUtil.containBracket(column)) {
            List<String> curColumnNames = StringUtil.extractBracket(column);
            if (!curColumnNames.isEmpty()) {
                column = curColumnNames.get(0);
            }
        }
        if (SqlUtil.checkIsColumn(column)) {
            String[] strArr = column.split(SymbolConstant.SYMBOL_BACK_SLASH + SymbolConstant.SYMBOL_POINT);
            TableNode curTableNode = null;
            String curColumnName = null;
            if (strArr.length > 1) {
                curTableNode = aliasTable.get(strArr[0]);
                curColumnName = strArr[1];
            } else if (aliasTable.size() == 1) {
                Iterator<TableNode> tableNodeIterator = aliasTable.values().iterator();
                curTableNode = tableNodeIterator.next();
                curColumnName = strArr[0];
            }
            if (curTableNode == null) {
                new MapperException(ExceptionCommonConstant.TABLE_NOT_EXIST,
                        errMsg + String.format(MessageConstant.EXPRESS_MSG, element.toString())).printException();
            }
            boolean exist = columnDao.checkColumnExist(curColumnName, curTableNode.getTableName());
            if (!exist) {
                new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                        errMsg + String.format(MessageConstant.EXPRESS_MSG, element.toString())).printException();
            }
        }
    }

    /**
     * Check condition node column exist.
     * @author wangyi8
     * @taskId
     * @param aliasTable the alias table
     * @param element the element
     * @param columnDao the column dao
     * @param errMsg the err msg
     */
    public void checkConditionNodeColumnExist(Map<String, TableNode> aliasTable, ConditionNode element, ColumnDao columnDao, String errMsg) {
        // 排除exists和not exists的情况, 内部sql会单独做验证，这里不做
        if (element.getColumnName().equals(SqlConstant.EXISTS) || element.getConditionType().equals(SqlConstant.EXISTS)) {
            return;
        }
        //验证是否符合字段条件，存在类似 1=1 的恒等比较，需要过滤掉
        if (SqlUtil.checkIsColumn(element.getColumnName())) {
            checkColumnExist(element, element.getColumnName(), aliasTable, columnDao, errMsg);
        }
        // 验证两表关联的比较
        if (SqlUtil.checkIsColumn(element.getValue())) {
            checkColumnExist(element, element.getValue(), aliasTable, columnDao, errMsg);
        }
    }

    /**
     * Check query node column exist.
     * @author wangyi8
     * @taskId
     * @param aliasTable the alias table
     * @param element the element
     * @param columnDao the column dao
     * @param errMsg the err msg
     */
    public void checkQueryNodeColumnExist(Map<String, TableNode> aliasTable, QueryNode element, ColumnDao columnDao, String errMsg) {
        TableNode tableNode = getTableNameByAlias(aliasTable, element.getTableAlias());
        String columnName = element.getColumnName();
        if (tableNode == null) {
            new MapperException(ExceptionCommonConstant.TABLE_ALIAS_IS_NULL,
                    errMsg + String.format(MessageConstant.EXPRESS_MSG, element.toString())).printException();
            return;
        } else {
            if (tableNode.isCanCheck() && SqlUtil.checkIsColumn(columnName)) {
                boolean exist = columnDao.checkColumnExist(columnName, tableNode.getTableName());
                if (!exist) {
                    new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                            errMsg + String.format(MessageConstant.EXPRESS_MSG, element.toString())).printException();
                }
            }
        }
    }


    /**
     * Check function node column exist.
     * @author wangyi8
     * @taskId
     * @param aliasTable the alias table
     * @param element the element
     * @param columnDao the column dao
     * @param errMsg the err msg
     */
    public void checkFunctionNodeColumnExist(Map<String, TableNode> aliasTable, FunctionNode element, ColumnDao columnDao, String errMsg) {
        List<FunctionNode.InnerNode> innerNodeList = element.getInnerNodeList();
        if (innerNodeList != null) {
            for (FunctionNode.InnerNode node : innerNodeList) {
                TableNode tableNode = getTableNameByAlias(aliasTable, node.getTableAlias());
                String columnName = node.getColumnName();
                if (tableNode == null) {
                    new MapperException(ExceptionCommonConstant.TABLE_ALIAS_IS_NULL,
                            errMsg + String.format(MessageConstant.EXPRESS_MSG, element.toString())).printException();
                    continue;
                } else {
                    if (tableNode.isCanCheck()) {
                        boolean exist = columnDao.checkColumnExist(columnName, tableNode.getTableName());
                        if (!exist) {
                            new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                                    errMsg + String.format(MessageConstant.EXPRESS_MSG, columnName)).printException();
                        }
                    }
                }
            }
        }
    }

    /**
     * 通过别称获取表名
     * @param aliasTable 表信息
     * @param alias 表别称
     * @return 表节点
     */
    public TableNode getTableNameByAlias(Map<String, TableNode> aliasTable, String alias) {
        if (StringUtil.isBlank(alias)) {
            return aliasTable.get(MessageConstant.CURRENT_TABLE);
        } else {
            return aliasTable.get(alias);
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
                aliasTable.put(tableNode.getTableAlias(), tableNode);
            } else {
                if (tableNodes.size() > 1) {
                    new MapperException(ExceptionCommonConstant.TABLE_ALIAS_IS_NULL,
                            errMsg + String.format(MessageConstant.TABLE_NAME, tableNode.getTableName())).printException();
                } else {
                    aliasTable.put(tableNode.getTableName().toLowerCase(), tableNode);
                }
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
        Map<String, TableNode> aliasTable = new HashMap<String, TableNode>();
        if (tableNodeList != null && !tableNodeList.isEmpty()) {
            TableNode tableNode = tableNodeList.get(0);
            if (tableNode != null) {
                aliasTable.put(MessageConstant.CURRENT_TABLE, tableNode);
                tableName = tableNode.getTableName() == null ? "" : tableNode.getTableName();
            }
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
            if (node == null || node.getIfContent() == null) {
                continue;
            }
            String columnName = node.getNode().getColumnName();
            boolean result = columnDao.checkColumnExist(columnName, tableName);
            if (!result) {
                new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                        item.getInfoMessage() + String.format(MessageConstant.TABLE_COLUMN_NAME, tableName, columnName)
                                + String.format(MessageConstant.EXPRESS_MSG, node.getIfContent())).printException();
            }
        }

        List<SelectElement> selectElements = item.getWhereConditions();
        for (SelectElement element : selectElements) {
            if (element instanceof ConditionNode) {
                checkConditionNodeColumnExist(aliasTable, (ConditionNode) element, columnDao, item.getInfoMessage());
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
            InsertNode insertNode = item.getInsertNode();
            ColumnNode columnNode = insertNode.getColumnNode();
            for (SelectElement selectElement : columnNode.getSelectElementList()) {
                if (selectElement instanceof QueryNode) {
                    String columnName = ((QueryNode) selectElement).getColumnName();
                    boolean result = columnDao.checkColumnExist(columnName, tableName);
                    if (!result) {
                        new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                                item.getInfoMessage() +
                                        String.format(MessageConstant.TABLE_COLUMN_NAME, tableName, columnName)
                                        + String.format(MessageConstant.COLUMN_NAME, columnName)).printException();
                        continue;
                    }
                }
            }
        }
        for (int i = 0; i < ifColumnNodes.size(); i++) {
            InsertIfColumnNode columnNode = ifColumnNodes.get(i);
            if (columnNode.getIfContent() == null) {
                new MapperException(ExceptionCommonConstant.IF_TAG_EXPLAIN_ERROR, item.getInfoMessage() +
                        String.format(MessageConstant.EXPRESS_MSG, columnNode.getContents())).printException();
                continue;
            }
            if (!columnNode.getIfContent().trim().endsWith(SymbolConstant.SYMBOL_COMMA)) {
                new MapperException(ExceptionCommonConstant.INSERT_END_WITH_COMMA, item.getInfoMessage() +
                        String.format(MessageConstant.EXPRESS_MSG, columnNode.getIfContent())).printException();
                continue;
            }
            String columnName = columnNode.getIfContent().replace(SymbolConstant.SYMBOL_COMMA, "").trim();
            boolean result = columnDao.checkColumnExist(columnName, tableName);
            if (!result) {
                new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                        item.getInfoMessage() +
                                String.format(MessageConstant.TABLE_COLUMN_NAME, tableName, columnName)
                                + String.format(MessageConstant.EXPRESS_MSG, columnNode.getIfContent())).printException();
                continue;
            }
        }
    }

    /**
     * 验证表字段是否存在
     * @param element 验证对象
     * @param express 需要验证的表达式
     * @param aliasTable 表信息
     * @param columnDao 字段查询dao
     * @param errMsg 异常信息
     */
    private void checkColumnExist(ConditionNode element, String express, Map<String, TableNode> aliasTable, ColumnDao columnDao, String errMsg) {
        String[] strArr = express.split(SymbolConstant.SYMBOL_BACK_SLASH + SymbolConstant.SYMBOL_POINT);
        TableNode curTableNode = null;
        String curColumnName = null;
        if (strArr.length > 1) {
            curTableNode = aliasTable.get(strArr[0]);
            curColumnName = strArr[1];
        } else if (aliasTable.size() == 1) {
            Iterator<TableNode> tableNodeIterator = aliasTable.values().iterator();
            curTableNode = tableNodeIterator.next();
            curColumnName = strArr[0];
        } else if (aliasTable.containsKey(MessageConstant.CURRENT_TABLE)) {
            curTableNode = aliasTable.get(MessageConstant.CURRENT_TABLE);
            curColumnName = strArr[0];
        }
        if (curTableNode == null) {
            new MapperException(ExceptionCommonConstant.TABLE_ALIAS_IS_NULL,
                    errMsg + String.format(MessageConstant.EXPRESS_MSG, element.toString())).printException();
        } else if (curTableNode.isCanCheck()) {
            if (SqlUtil.checkIsSimpleTable(curTableNode.getTableName())) {
                boolean exist = columnDao.checkColumnExist(curColumnName, curTableNode.getTableName());
                if (!exist) {
                    new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                            errMsg + String.format(MessageConstant.EXPRESS_MSG, element.toString())).printException();
                }
            }
        }
    }
}
