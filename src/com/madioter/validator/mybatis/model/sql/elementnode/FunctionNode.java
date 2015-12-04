package com.madioter.validator.mybatis.model.sql.elementnode;

import com.madioter.validator.mybatis.config.statement.SelectMappedStatementItem;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.SqlUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.Iterator;
import java.util.Map;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月25日 <br>
 */
public class FunctionNode implements SelectElement {

    /**
     * 异常表达式
     */
    private static final String SQL_EXPRESS_TEXT = "表达式: %s";

    /**
     * 函数表达式
     */
    private String express;

    /**
     * 别称
     */
    private String alias;

    /**
     * Gets express.
     * @return express express
     */
    public String getExpress() {
        return express;
    }

    /**
     * Sets express.
     * @param express the express
     */
    public void setExpress(String express) {
        this.express = express;
    }

    /**
     * Gets alias.
     * @return alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets alias.
     * @param alias the alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * 自验证方法
     * @param aliasTable 表定义
     * @param columnDao  数据库字段操作类
     * @param errMsg 异常信息
     */
    public void validate(Map<String, TableNode> aliasTable, ColumnDao columnDao, String errMsg) {
        //1、判断函数是否为当前数据库允许的函数名 TODO

        //2、解析函数中用到的字段，一般在括号中，逗号间隔后，排除关键字DISTINCT
        String[] str = this.express.substring(this.express.indexOf(SymbolConstant.SYMBOL_LEFT_BRACKET),
                this.express.indexOf(SymbolConstant.SYMBOL_RIGHT_BRACKET)).split(SymbolConstant.SYMBOL_COMMA);
        for (int i = 0; i < str.length; i++) {
            String columnExp = null;
            if (str[i].contains(SqlConstant.DISTINCT + SymbolConstant.SYMBOL_BLANK)) {
                columnExp = str[i].replace(SqlConstant.DISTINCT + SymbolConstant.SYMBOL_BLANK, "");
            } else {
                columnExp = str[i];
            }
            if (SqlUtil.checkIsColumn(columnExp)) {
                String[] strArr = columnExp.split(SymbolConstant.SYMBOL_POINT);
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
                            errMsg + String.format(SQL_EXPRESS_TEXT, express)).printException();
                } else if (curTableNode.isCanCheck()) {
                    boolean exist = columnDao.checkColumnExist(curColumnName, curTableNode.getTableName());
                    if (!exist) {
                        new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                                errMsg + String.format(SQL_EXPRESS_TEXT, express)).printException();
                    }
                }
            }
        }
    }
}
