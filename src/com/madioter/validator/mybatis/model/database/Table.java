package com.madioter.validator.mybatis.model.database;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月12日 <br>
 */
public class Table {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表Schema
     */
    private String tableSchema;

    /**
     * 表字段列表
     */
    private List<Column> columnList;

    /**
     * Instantiates a new Table.
     */
    public Table() {

    }

    /**
     * Instantiates a new Table.
     *
     * @param tableName the table name
     * @param tableSchema the table schema
     */
    public Table(String tableName, String tableSchema) {
        this.tableName = tableName;
        this.tableSchema = tableSchema;
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
     * Gets table schema.
     * @return the table schema
     */
    public String getTableSchema() {
        return tableSchema;
    }

    /**
     * Sets table schema.
     * @param tableSchema the table schema
     */
    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    @Override
    public boolean equals(Object table) {
        //判断对象类型是否正确
        if (table == null || !(table instanceof Table)) {
            return false;
        } else if (((Table) table).getTableName().equals(this.tableName)) {
            //判断表名是否正确，表名正确后判断表Schema是否一致
            if (this.tableSchema == null || ((Table) table).getTableSchema().equals(this.tableSchema)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Gets column list.
     * @return the column list
     */
    public List<Column> getColumnList() {
        return columnList;
    }

    /**
     * Sets column list.
     * @param columnList the column list
     */
    public void setColumnList(List<Column> columnList) {
        this.columnList = columnList;
    }
}
