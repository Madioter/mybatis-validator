package com.madioter.validator.mybatis.config.selectnode;

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
}
