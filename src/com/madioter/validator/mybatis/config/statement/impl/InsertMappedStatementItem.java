package com.madioter.validator.mybatis.config.statement.impl;

import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfColumnNode;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfValueNode;
import com.madioter.validator.mybatis.model.sql.sqltag.component.ISqlComponent;
import com.madioter.validator.mybatis.model.sql.sqltag.component.IfSqlComponent;
import com.madioter.validator.mybatis.model.sql.sqltag.component.MixedSqlComponent;
import com.madioter.validator.mybatis.model.sql.sqltag.component.TrimSqlComponent;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <Description> insert标签<br>
 * 判断内容：
 *  1、表及字段是否存在
 *  2、参数类及属性是否存在
 *  3、参数类及属性对应顺序及关系是否一致（例如column多了，value少了）
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月20日 <br>
 */
public class InsertMappedStatementItem extends MappedStatementItem {

    /**
     * insert的表名
     */
    private TableNode tableNode;

    /**
     * 保存对象类型
     */
    private Class parameterType;

    /**
     * insert的字段
     */
    private List<InsertIfColumnNode> ifColumnNodeList = new ArrayList<InsertIfColumnNode>();

    /**
     * 用于解析insert字段名的标签
     */
    private Object columnSqlNode;

    /**
     * insert的赋值节点
     */
    private List<InsertIfValueNode> ifValueNodeList = new ArrayList<InsertIfValueNode>();

    /**
     * 用于解析insert value赋值的节点
     */
    private Object valueSqlNode;

    /**
     * 构造方法
     * @param mappedStatement 原数据
     * @throws ConfigException 配置异常
     */
    public InsertMappedStatementItem(MappedStatement mappedStatement) throws ConfigException {
        super(mappedStatement);
        this.parameterType = getMappedStatement().getParameterMap().getType();
        rebuild();
    }

    @Override
    public List<TableNode> getTableNodes() {
        List<TableNode> tableNodes = new ArrayList<TableNode>();
        if (tableNode != null) {
            tableNodes.add(tableNode);
        }
        return tableNodes;
    }

    /**
     * Rebuild.
     * @author wangyi8
     * @taskId
     * @throws ConfigException the config exception
     */
    private void rebuild() throws ConfigException {
        List<String> fragment = StringUtil.arrayToList(StringUtil.splitWithBlank(getSql()));
        for (int i = 0; i < fragment.size(); i++) {
            if (fragment.get(i).equals(SqlConstant.INSERT) && i < fragment.size() - 2) {
                //insert into
                String tableName = fragment.get(i + 2);
                tableNode = new TableNode();
                tableNode.setTableName(tableName);
            }
        }

        List<ISqlComponent> sqlComponents = getSqlComponentList();
        for (ISqlComponent sqlComponent : sqlComponents) {
            if (sqlComponent instanceof TrimSqlComponent) {
                if (this.columnSqlNode == null) {
                    convertIfColumnNodeList((TrimSqlComponent) sqlComponent);
                } else if (this.valueSqlNode == null) {
                    convertIfValueNodeList((TrimSqlComponent) sqlComponent);
                }
            }
        }
    }

    /**
     * Convert if column node list.
     * @author wangyi8
     * @taskId
     * @param sqlComponent the sql component
     * @throws ConfigException the config exception
     */
    private void convertIfColumnNodeList(TrimSqlComponent sqlComponent) throws ConfigException {
        this.columnSqlNode = sqlComponent.getTrimSqlNode();
        ISqlComponent innerComponent = sqlComponent.getContent();
        if (innerComponent instanceof MixedSqlComponent) {
            List<ISqlComponent> list = ((MixedSqlComponent) innerComponent).getContents();
            for (ISqlComponent item : list) {
                if (item instanceof IfSqlComponent) {
                    ifColumnNodeList.add(new InsertIfColumnNode(((IfSqlComponent) item).getIfSqlNode()));
                }
            }
        }
    }

    /**
     * Convert if value node list.
     * @author wangyi8
     * @taskId
     * @param sqlComponent the sql component
     * @throws ConfigException the config exception
     */
    private void convertIfValueNodeList(TrimSqlComponent sqlComponent) throws ConfigException {
        this.columnSqlNode = sqlComponent.getTrimSqlNode();
        ISqlComponent innerComponent = sqlComponent.getContent();
        if (innerComponent instanceof MixedSqlComponent) {
            List<ISqlComponent> list = ((MixedSqlComponent) innerComponent).getContents();
            for (ISqlComponent item : list) {
                if (item instanceof IfSqlComponent) {
                    ifValueNodeList.add(new InsertIfValueNode(((IfSqlComponent) item).getIfSqlNode()));
                }
            }
        }
    }

    /**
     * Gets if column node list.
     * @return the if column node list
     */
    public List<InsertIfColumnNode> getIfColumnNodeList() {
        return ifColumnNodeList;
    }

    /**
     * Sets if column node list.
     * @param ifColumnNodeList the if column node list
     */
    public void setIfColumnNodeList(List<InsertIfColumnNode> ifColumnNodeList) {
        this.ifColumnNodeList = ifColumnNodeList;
    }

    /**
     * Gets if value node list.
     * @return the if value node list
     */
    public List<InsertIfValueNode> getIfValueNodeList() {
        return ifValueNodeList;
    }

    /**
     * Sets if value node list.
     * @param ifValueNodeList the if value node list
     */
    public void setIfValueNodeList(List<InsertIfValueNode> ifValueNodeList) {
        this.ifValueNodeList = ifValueNodeList;
    }

    /**
     * Sets value sql node.
     * @param valueSqlNode the value sql node
     */
    public void setValueSqlNode(Object valueSqlNode) {
        this.valueSqlNode = valueSqlNode;
    }

    /**
     * Sets column sql node.
     * @param columnSqlNode the column sql node
     */
    public void setColumnSqlNode(Object columnSqlNode) {
        this.columnSqlNode = columnSqlNode;
    }


    /**
     * Gets column sql node.
     * @return the column sql node
     */
    public Object getColumnSqlNode() {
        return columnSqlNode;
    }

    /**
     * Gets value sql node.
     * @return the value sql node
     */
    public Object getValueSqlNode() {
        return valueSqlNode;
    }


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
     * Gets parameter type.
     * @return the parameter type
     */
    public Class getParameterType() {
        return parameterType;
    }
}
