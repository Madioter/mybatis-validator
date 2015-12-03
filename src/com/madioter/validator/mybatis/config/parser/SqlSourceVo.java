package com.madioter.validator.mybatis.config.parser;

import com.madioter.validator.mybatis.config.tagnode.InsertIfColumnNode;
import com.madioter.validator.mybatis.config.tagnode.InsertIfValueNode;
import com.madioter.validator.mybatis.config.tagnode.SelectIfNode;
import com.madioter.validator.mybatis.config.tagnode.UpdateIfSetNode;
import java.util.List;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年12月01日 <br>
 */
public class SqlSourceVo {

    /**
     * 原sqlSource的引用
     */
    private SqlSource sqlSource;

    /**
     * 标准化的sql语句
     */
    private String sql = "";

    /**
     * if条件
     */
    private List<SelectIfNode> selectIfNodeList;

    /**
     * 参数定义
     */
    private List<ParameterMapping> parameterMappings;

    /**
     * update Set 结构
     */
    private List<UpdateIfSetNode> setNodeList;

    /**
     * setSqlNode
     */
    private Object setSqlNode;

    /**
     * insert 的 column 标签
     */
    private List<InsertIfColumnNode> ifColumnNodes;

    /**
     * insert 的 value 标签
     */
    private List<InsertIfValueNode> ifValueNodes;

    /**
     * columnSqlNode
     */
    private Object columnSqlNode;

    /**
     * valueSqlNode
     */
    private Object valueSqlNode;

    /**
     * Sets sql.
     * @param sql the sql
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Gets sql.
     * @return the sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * Sets select if node list.
     * @param selectIfNodeList the select if node list
     */
    public void setSelectIfNodeList(List<SelectIfNode> selectIfNodeList) {
        this.selectIfNodeList = selectIfNodeList;
    }

    /**
     * Gets select if node list.
     * @return the select if node list
     */
    public List<SelectIfNode> getSelectIfNodeList() {
        return selectIfNodeList;
    }

    /**
     * Sets parameter mappings.
     * @param parameterMappings the parameter mappings
     */
    public void setParameterMappings(List<ParameterMapping> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }

    /**
     * Gets parameter mappings.
     * @return the parameter mappings
     */
    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    /**
     * Gets sql source.
     * @return sql source
     */
    public SqlSource getSqlSource() {
        return sqlSource;
    }

    /**
     * Sets sql source.
     * @param sqlSource the sql source
     */
    public void setSqlSource(SqlSource sqlSource) {
        this.sqlSource = sqlSource;
    }

    /**
     * Gets set node list.
     * @return the set node list
     */
    public List<UpdateIfSetNode> getSetNodeList() {
        return setNodeList;
    }

    /**
     * Sets set node list.
     * @param setNodeList the set node list
     */
    public void setSetNodeList(List<UpdateIfSetNode> setNodeList) {
        this.setNodeList = setNodeList;
    }

    /**
     * Sets if column nodes.
     * @param ifColumnNodes the if column nodes
     */
    public void setIfColumnNodes(List<InsertIfColumnNode> ifColumnNodes) {
        this.ifColumnNodes = ifColumnNodes;
    }

    /**
     * Gets if column nodes.
     * @return the if column nodes
     */
    public List<InsertIfColumnNode> getIfColumnNodes() {
        return ifColumnNodes;
    }

    /**
     * Sets if value nodes.
     * @param ifValueNodes the if value nodes
     */
    public void setIfValueNodes(List<InsertIfValueNode> ifValueNodes) {
        this.ifValueNodes = ifValueNodes;
    }

    /**
     * Gets if value nodes.
     * @return the if value nodes
     */
    public List<InsertIfValueNode> getIfValueNodes() {
        return ifValueNodes;
    }

    /**
     * Sets if column node.
     * @param columnSqlNode the if column node
     */
    public void setColumnSqlNode(Object columnSqlNode) {
        this.columnSqlNode = columnSqlNode;
    }

    /**
     * Gets if column node.
     * @return the if column node
     */
    public Object getColumnSqlNode() {
        return columnSqlNode;
    }

    /**
     * Sets if value node.
     * @param valueSqlNode the if value node
     */
    public void setValueSqlNode(Object valueSqlNode) {
        this.valueSqlNode = valueSqlNode;
    }

    /**
     * Gets if value node.
     * @return the if value node
     */
    public Object getValueSqlNode() {
        return valueSqlNode;
    }

    /**
     * Gets set sql node.
     * @return set sql node
     */
    public Object getSetSqlNode() {
        return setSqlNode;
    }

    /**
     * Sets set sql node.
     * @param setSqlNode the set sql node
     */
    public void setSetSqlNode(Object setSqlNode) {
        this.setSqlNode = setSqlNode;
    }
}
