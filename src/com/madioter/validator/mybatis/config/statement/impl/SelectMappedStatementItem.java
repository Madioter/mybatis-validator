package com.madioter.validator.mybatis.config.statement.impl;

import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.model.java.ClassModel;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.SelectNode;
import com.madioter.validator.mybatis.model.sql.sqltag.SelectIfNode;
import com.madioter.validator.mybatis.parser.sqlparser.SelectSqlParser;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月20日 <br>
 */
public class SelectMappedStatementItem extends MappedStatementItem {

    /**
     * 返回对象
     */
    private ClassModel classModel;

    /**
     * 保存对象类型
     */
    private Class parameterType;

    /**
     * 查询结构
     */
    private List<SelectNode> selectNodeList;

    /**
     * 构造方法
     * @param mappedStatement 原数据
     * @throws ConfigException 配置异常
     */
    public SelectMappedStatementItem(MappedStatement mappedStatement) throws ConfigException {
        super(mappedStatement);
        this.parameterType = getMappedStatement().getParameterMap().getType();

        //使用select语句的sql解析器进行sql解析
        SelectSqlParser selectSqlParser = new SelectSqlParser(getSql());
        this.selectNodeList = selectSqlParser.getSelectNodeList();
    }

    @Override
    public List<TableNode> getTableNodes() {
        List<TableNode> tableNodes = new ArrayList<TableNode>();
        for (SelectNode selectNode : selectNodeList) {
            tableNodes.addAll(selectNode.getTableNodes());
        }
        return tableNodes;
    }

    /**
     * Gets parameter type.
     * @return parameter type
     */
    public Class getParameterType() {
        return parameterType;
    }

    /**
     * Sets parameter type.
     * @param parameterType the parameter type
     */
    public void setParameterType(Class parameterType) {
        this.parameterType = parameterType;
    }


    /**
     * Gets select node list.
     * @return the select node list
     */
    public List<SelectNode> getSelectNodeList() {
        return selectNodeList;
    }
}
