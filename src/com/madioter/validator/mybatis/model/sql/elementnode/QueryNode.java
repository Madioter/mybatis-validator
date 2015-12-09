package com.madioter.validator.mybatis.model.sql.elementnode;

import com.madioter.validator.mybatis.config.statement.SelectMappedStatementItem;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.SqlUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.Iterator;
import java.util.Map;

/**
 * <Description> 查询字段部分<br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月25日 <br>
 */
public class QueryNode implements SelectElement {

    /**
     * 异常表达式
     */
    private static final String SQL_EXPRESS_TEXT = "表达式: %s";

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段别称
     */
    private String columnAlias;

    /**
     * 表别称
     */
    private String tableAlias;

    /**
     * Gets column name.
     * @return column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets column name.
     * @param columnName the column name
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Gets table name.
     * @return the table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets table name.
     * @param tableName the table name
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Gets column alias.
     * @return column alias
     */
    public String getColumnAlias() {
        return columnAlias;
    }

    /**
     * Sets column alias.
     * @param columnAlias the column alias
     */
    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    /**
     * 结构重构
     */
    public void rebuild() {
        String[] strArr = columnName.split(SymbolConstant.SYMBOL_SLASH + SymbolConstant.SYMBOL_POINT);
        if (strArr.length > 1) {
            this.tableAlias = strArr[0];
            this.columnName = strArr[1];
        }
    }

    /**
     * 自验证方法
     * @param aliasTable 表定义
     * @param columnDao  数据库字段操作类
     * @param errMsg 异常信息
     */
    public void validate(Map<String, TableNode> aliasTable, ColumnDao columnDao, String errMsg) {
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
            } else if (aliasTable.containsKey(MessageConstant.CURRENT_TABLE)) {
                curTableNode = aliasTable.get(MessageConstant.CURRENT_TABLE);
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
     * Gets table alias.
     * @return the table alias
     */
    public String getTableAlias() {
        return tableAlias;
    }

    /**
     * Sets table alias.
     * @param tableAlias the table alias
     */
    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }
}
