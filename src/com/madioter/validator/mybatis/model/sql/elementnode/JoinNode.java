package com.madioter.validator.mybatis.model.sql.elementnode;

/**
 * <Description> 关联关系 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月25日 <br>
 */
public class JoinNode implements SelectElement {

    /**
     * 当前表名
     */
    private String currentTableName;

    /**
     * 当前表关联字段名
     */
    private String currentColumnName;

    /**
     * 关联表名
     */
    private String connectTableName;

    /**
     * 关联表关联字段名
     */
    private String connectColumnName;

    /**
     * Gets current table name.
     * @return current table name
     */
    public String getCurrentTableName() {
        return currentTableName;
    }

    /**
     * Sets current table name.
     * @param currentTableName the current table name
     */
    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
    }

    /**
     * Gets current column name.
     * @return the current column name
     */
    public String getCurrentColumnName() {
        return currentColumnName;
    }

    /**
     * Sets current column name.
     * @param currentColumnName the current column name
     */
    public void setCurrentColumnName(String currentColumnName) {
        this.currentColumnName = currentColumnName;
    }

    /**
     * Gets connect table name.
     * @return the connect table name
     */
    public String getConnectTableName() {
        return connectTableName;
    }

    /**
     * Sets connect table name.
     * @param connectTableName the connect table name
     */
    public void setConnectTableName(String connectTableName) {
        this.connectTableName = connectTableName;
    }

    /**
     * Gets connect column name.
     * @return the connect column name
     */
    public String getConnectColumnName() {
        return connectColumnName;
    }

    /**
     * Sets connect column name.
     * @param connectColumnName the connect column name
     */
    public void setConnectColumnName(String connectColumnName) {
        this.connectColumnName = connectColumnName;
    }
}
