package com.madioter.validator.mybatis.config.selectnode;

/**
 * <Description> where条件部分<br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月25日 <br>
 */
public class ConditionNode implements SelectElement{

    /**
     * 条件字段
     */
    private String columnName;

    /**
     * 条件类型
     */
    private String conditionType;

    /**
     * 条件值
     */
    private String value;

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
     * Gets condition type.
     * @return the condition type
     */
    public String getConditionType() {
        return conditionType;
    }

    /**
     * Sets condition type.
     * @param conditionType the condition type
     */
    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    /**
     * Gets value.
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
