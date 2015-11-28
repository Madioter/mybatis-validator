package com.madioter.validator.mybatis.config.selectnode;

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
    private String tableName;

    /**
     * 表别称
     */
    private String tableAlias;

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
}
