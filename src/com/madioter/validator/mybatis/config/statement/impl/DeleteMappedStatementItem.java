package com.madioter.validator.mybatis.config.statement.impl;

import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.WhereNode;
import com.madioter.validator.mybatis.model.sql.sqltag.component.ISqlComponent;
import com.madioter.validator.mybatis.model.sql.sqltag.component.SetSqlComponent;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <Description> 删除语句 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public class DeleteMappedStatementItem extends MappedStatementItem {

    /**
     * 保存对象类型
     */
    private Class parameterType;

    /**
     * insert的表名
     */
    private TableNode tableNode;

    /**
     * where条件字符串
     */
    private List<SelectElement> whereConditions;

    /**
     * 构造方法
     * @param mappedStatement 原数据
     * @throws ConfigException 配置异常
     */
    public DeleteMappedStatementItem(MappedStatement mappedStatement) throws ConfigException {
        super(mappedStatement);
        this.parameterType = getMappedStatement().getParameterMap().getType();
        List<String> fragment = StringUtil.arrayToList(StringUtil.splitWithBlank(getSql()));
        if (!fragment.contains(SqlConstant.DELETE)) {
            new MapperException(ExceptionCommonConstant.NO_DELETE_TAG_ERROR, this.getInfoMessage()).printException();
        } else {
            boolean whereFlag = false;
            StringBuilder whereText = new StringBuilder();
            for (int i = 0; i < fragment.size(); i++) {
                if (fragment.get(i).equals(SqlConstant.DELETE) && i < fragment.size() - 2) {
                    //delete from table （mysql中 from不可省略）
                    // TODO 支持ORACLE的 delete table
                    String tableName = fragment.get(i + 2);
                    tableNode = new TableNode();
                    tableNode.setTableName(tableName);
                } else if (fragment.get(i).equals(SqlConstant.WHERE)) {
                    whereFlag = true;
                } else if (whereFlag) {
                    whereText.append(fragment.get(i)).append(SymbolConstant.SYMBOL_BLANK);
                }
            }
            this.whereConditions = new WhereNode(
                    StringUtil.arrayToList(StringUtil.splitWithBlank(whereText.toString()))).getSelectElementList();
        }
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
     * Gets parameter type.
     * @return the parameter type
     */
    public Class getParameterType() {
        return parameterType;
    }

    /**
     * Gets where conditions.
     * @return the where conditions
     */
    public List<SelectElement> getWhereConditions() {
        return whereConditions;
    }
}
