package com.madioter.validator.mybatis.model.sql.sqlnode;

import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import java.util.List;

/**
 * <Description> <br>
 *
 *     INSERT INTO target [(field1[, field2[, ...]])][IN外部数据库] SELECT field1[, field2[, ...]] FROM tableexpression
 *     INSERT INTO target [(field1[, field2[, ...]])] VALUES (value1[, value2[, ...])
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月14日 <br>
 */
public class InsertNode {

    /**
     * 表
     */
    private TableNode tableNode;

    /**
     * field字段
     */
    private ColumnNode columnNode;

    /**
     * 赋值
     */
    private ValuesNode valueNode;

    /**
     * 赋值sql
     */
    private List<SelectNode> sqlNodeList;

    /**
     * Gets table node.
     * @return the table node
     */
    public TableNode getTableNode() {
        return tableNode;
    }

    /**
     * Sets table node.
     * @param tableNode the table node
     */
    public void setTableNode(TableNode tableNode) {
        this.tableNode = tableNode;
    }

    /**
     * Gets column node.
     * @return the column node
     */
    public ColumnNode getColumnNode() {
        return columnNode;
    }

    /**
     * Sets column node.
     * @param columnNode the column node
     */
    public void setColumnNode(ColumnNode columnNode) {
        this.columnNode = columnNode;
    }

    /**
     * Gets value node.
     * @return the value node
     */
    public ValuesNode getValueNode() {
        return valueNode;
    }

    /**
     * Sets value node.
     * @param valueNode the value node
     */
    public void setValueNode(ValuesNode valueNode) {
        this.valueNode = valueNode;
    }

    /**
     * Sets sql node list.
     * @param sqlNodeList the sql node list
     */
    public void setSqlNodeList(List<SelectNode> sqlNodeList) {
        this.sqlNodeList = sqlNodeList;
    }

    /**
     * Gets sql node list.
     * @return the sql node list
     */
    public List<SelectNode> getSqlNodeList() {
        return sqlNodeList;
    }
}
