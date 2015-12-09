package com.madioter.validator.mybatis.model.sql.elementnode;

import com.madioter.validator.mybatis.config.statement.SelectMappedStatementItem;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.SqlUtil;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.Iterator;
import java.util.List;
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
     * 函数表达式
     */
    private String express;

    /**
     * 别称
     */
    private String alias;

    /**
     * 内部结构
     */
    private List<InnerNode> innerNodeList;

    /**
     * The type Inner node.
     */
    public class InnerNode {
        /**
         * 字段名
         */
        private String columnName;

        /**
         * 表别称
         */
        private String tableAlias;

        /**
         * Gets column name.
         * @return the column name
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
     * Gets inner node list.
     * @return the inner node list
     */
    public List<InnerNode> getInnerNodeList() {
        return innerNodeList;
    }

    /**
     * Sets inner node list.
     * @param innerNodeList the inner node list
     */
    public void setInnerNodeList(List<InnerNode> innerNodeList) {
        this.innerNodeList = innerNodeList;
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
                } else if (aliasTable.containsKey(MessageConstant.CURRENT_TABLE)) {
                    curTableNode = aliasTable.get(MessageConstant.CURRENT_TABLE);
                    curColumnName = strArr[0];
                }
                if (curTableNode == null) {
                    new MapperException(ExceptionCommonConstant.TABLE_ALIAS_IS_NULL,
                            errMsg + String.format(MessageConstant.EXPRESS_MSG, express)).printException();
                } else if (curTableNode.isCanCheck()) {
                    boolean exist = columnDao.checkColumnExist(curColumnName, curTableNode.getTableName());
                    if (!exist) {
                        new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                                errMsg + String.format(MessageConstant.EXPRESS_MSG, express)).printException();
                    }
                }
            }
        }
    }

    @Override
    public void rebuild() {
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
                InnerNode node = new InnerNode();
                String[] strArr = columnExp.split(SymbolConstant.SYMBOL_POINT);
                if (strArr.length > 1) {
                    node.setColumnName(strArr[1]);
                    node.setTableAlias(strArr[0]);
                } else if (!StringUtil.isBlank(columnExp)) {
                    node.setColumnName(columnExp);
                }
                if (!StringUtil.isBlank(node.getColumnName())) {
                    innerNodeList.add(node);
                }
            }
        }
    }
}
