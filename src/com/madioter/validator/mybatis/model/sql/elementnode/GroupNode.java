package com.madioter.validator.mybatis.model.sql.elementnode;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月28日 <br>
 */
public class GroupNode implements SelectElement {

    /**
     * 分组字段（先字符串，以后在解析）
     */
    private String columnNames = "";

    /**
     * 分组条件
     */
    private String havingConditions = "";


    /**
     * Gets column names.
     * @return column names
     */
    public String getColumnNames() {
        return columnNames;
    }

    /**
     * Sets column names.
     * @param columnNames the column names
     */
    public void setColumnNames(String columnNames) {
        this.columnNames = columnNames;
    }

    /**
     * Gets having conditions.
     * @return the having conditions
     */
    public String getHavingConditions() {
        return havingConditions;
    }

    /**
     * Sets having conditions.
     * @param havingConditions the having conditions
     */
    public void setHavingConditions(String havingConditions) {
        this.havingConditions = havingConditions;
    }
}
