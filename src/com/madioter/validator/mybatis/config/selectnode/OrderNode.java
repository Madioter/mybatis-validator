package com.madioter.validator.mybatis.config.selectnode;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月28日 <br>
 */
public class OrderNode implements SelectElement {

    /**
     * The enum Order type.
     */
    public enum OrderType {
        /**
         * 排序方式
         */
        DESC,ASC;
    }

    /**
     * 排序字段
     */
    private String orderColumn;

    /**
     * 排序方式
     */
    private OrderType orderType = OrderType.ASC;

    /**
     * Gets order column.
     * @return order column
     */
    public String getOrderColumn() {
        return orderColumn;
    }

    /**
     * Sets order column.
     * @param orderColumn the order column
     */
    public void setOrderColumn(String orderColumn) {
        this.orderColumn = orderColumn;
    }

    /**
     * Gets order type.
     * @return the order type
     */
    public OrderType getOrderType() {
        return orderType;
    }

    /**
     * Sets order type.
     * @param orderType the order type
     */
    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }
}
