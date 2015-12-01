package com.madioter.validator.mybatis.config.parser;

import com.madioter.validator.mybatis.config.tagnode.SelectIfNode;
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
}
