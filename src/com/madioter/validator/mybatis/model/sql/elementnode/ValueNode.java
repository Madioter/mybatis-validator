package com.madioter.validator.mybatis.model.sql.elementnode;

/**
 * <Description> Insert语句的values中的表达式 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月15日 <br>
 */
public class ValueNode implements SelectElement {

    /**
     * value
     */
    private String value;

    @Override
    public void rebuild() {
        return;
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

    @Override
    public String toString() {
        return value;
    }
}
