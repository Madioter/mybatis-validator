package com.madioter.validator.mybatis.model.sql.elementnode;

import com.madioter.validator.mybatis.database.ColumnDao;
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
 * @CreateDate 2015年11月28日 <br>
 */
public class OrderNode implements SelectElement {

    /**
     * 异常表达式
     */
    private static final String SQL_EXPRESS_TEXT = "表达式: %s";

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
     * 自验证方法
     * @param aliasTable 表信息
     * @param columnDao 数据字段查询类
     * @param errMsg 异常信息
     */
    public void validate(Map<String, TableNode> aliasTable, ColumnDao columnDao, String errMsg) {
        String column = orderColumn;
        if (StringUtil.containBracket(orderColumn)) {
            List<String> curColumnNames = StringUtil.extractBracket(orderColumn);
            if (!curColumnNames.isEmpty()) {
                column = curColumnNames.get(0);
            }
        }
        if (SqlUtil.checkIsColumn(column)) {
            String[] strArr = column.split("\\" + SymbolConstant.SYMBOL_POINT);
            TableNode curTableNode = null;
            String curColumnName = null;
            if (strArr.length > 1) {
                curTableNode = aliasTable.get(strArr[0]);
                curColumnName = strArr[1];
            } else if (aliasTable.size() == 1) {
                Iterator<TableNode> tableNodeIterator = aliasTable.values().iterator();
                curTableNode = tableNodeIterator.next();
                curColumnName = strArr[0];
            }
            if (curTableNode == null) {
                new MapperException(ExceptionCommonConstant.TABLE_NOT_EXIST,
                        errMsg + String.format(SQL_EXPRESS_TEXT, this.orderColumn)).printException();
            }
            boolean exist = columnDao.checkColumnExist(curColumnName, curTableNode.getTableName());
            if (!exist) {
                new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                        errMsg + String.format(SQL_EXPRESS_TEXT, this.orderColumn)).printException();
            }
        }

    }

    @Override
    public String toString() {
        return orderColumn + SymbolConstant.SYMBOL_BLANK + orderType;
    }

    @Override
    public void rebuild() {
        return;
    }

    @Override
    public Boolean getIsComplete() {
        return true;
    }

    /**
     * The enum Order type.
     */
    public enum OrderType {
        /**
         * 排序方式
         */
        DESC, ASC;
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
