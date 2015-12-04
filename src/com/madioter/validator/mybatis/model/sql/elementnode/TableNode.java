package com.madioter.validator.mybatis.model.sql.elementnode;

import com.madioter.validator.mybatis.database.TableDao;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月25日 <br>
 */
public class TableNode implements SelectElement {

    /**
     * 表名
     */
    private static final String TABLE_NAME = " 表名：%s";

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表别称
     */
    private String tableAlias;

    /**
     * 是否可以被验证
     */
    private boolean canCheck = true;

    /**
     * Gets table name.
     * @return table name
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

    /**
     * 表验证
     *
     * @author wangyi8 * @taskId *
     * @param tableDao 表操作DAO
     * @param errMsg 异常信息
     */
    public void validate(TableDao tableDao, String errMsg) {
        //排除 @select#0@ 内部sql表 和 (select 1 as v) 内部表的操作
        if (!tableName.contains(SymbolConstant.SYMBOL_LEFT_BRACKET) && !tableName.contains(SymbolConstant.SYMBOL_AT)) {
            boolean exist = tableDao.checkExist(this.getTableName());
            if (!exist) {
                new MapperException(ExceptionCommonConstant.TABLE_NOT_EXIST,
                        errMsg + String.format(TABLE_NAME, this.getTableName())).printException();
            }
        } else {
            //设置为不可被验证
            canCheck = false;
        }
    }

    /**
     * Is can check boolean.
     * @return boolean
     */
    public boolean isCanCheck() {
        return canCheck;
    }
}
