package com.madioter.validator.mybatis.config.statement.impl;

import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.WhereNode;
import com.madioter.validator.mybatis.model.sql.sqltag.UpdateIfSetNode;
import com.madioter.validator.mybatis.model.sql.sqltag.component.ISqlComponent;
import com.madioter.validator.mybatis.model.sql.sqltag.component.IfSqlComponent;
import com.madioter.validator.mybatis.model.sql.sqltag.component.MixedSqlComponent;
import com.madioter.validator.mybatis.model.sql.sqltag.component.SetSqlComponent;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.StringUtil;
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
public class UpdateMappedStatementItem extends MappedStatementItem {

    /**
     * insert的表名
     */
    private TableNode tableNode;

    /**
     * 保存对象类型
     */
    private Class parameterType;

    /**
     * set中if节点对象列表
     */
    private List<UpdateIfSetNode> setNodeList = new ArrayList<UpdateIfSetNode>();

    /**
     * where条件字符串
     */
    private List<SelectElement> whereConditions;

    /**
     * 构造方法
     * @param mappedStatement 原数据
     * @throws ConfigException 配置异常
     */
    public UpdateMappedStatementItem(MappedStatement mappedStatement) throws ConfigException {
        super(mappedStatement);
        this.parameterType = getMappedStatement().getParameterMap().getType();
        rebuild();
    }


    /**
     * Rebuild.
     * @author wangyi8
     * @taskId
     * @throws ConfigException the config exception
     */
    private void rebuild() throws ConfigException {
        List<String> fragment = StringUtil.arrayToList(StringUtil.splitWithBlank(getSql()));
        boolean whereFlag = false;
        StringBuilder whereText = new StringBuilder();
        for (int i = 0; i < fragment.size(); i++) {
            if (fragment.get(i).equals(SqlConstant.UPDATE) && i < fragment.size() - 1) {
                tableNode = new TableNode();
                tableNode.setTableName(fragment.get(i + 1));
            } else if (fragment.get(i).equals(SqlConstant.WHERE)) {
                whereFlag = true;
            }
            if (whereFlag) {
                whereText.append(fragment.get(i));
            }
        }
        this.whereConditions = new WhereNode(
                StringUtil.arrayToList(StringUtil.splitWithBlank(whereText.toString()))).getSelectElementList();
        List<ISqlComponent> sqlComponents = getSqlComponentList();
        for (ISqlComponent sqlComponent : sqlComponents) {
            if (sqlComponent instanceof SetSqlComponent) {
                convertSetNodeList((SetSqlComponent) sqlComponent);
            }
        }
    }

    /**
     * Convert set node list.
     * @author wangyi8
     * @taskId
     * @param sqlComponent the sql component
     * @throws ConfigException exception
     */
    private void convertSetNodeList(SetSqlComponent sqlComponent) throws ConfigException {
        ISqlComponent innerComponent = sqlComponent.getContent();
        if (innerComponent instanceof MixedSqlComponent) {
            List<ISqlComponent> sqlComponents = ((MixedSqlComponent) innerComponent).getContents();
            for (ISqlComponent item : sqlComponents) {
                if (item instanceof IfSqlComponent) {
                    setNodeList.add(new UpdateIfSetNode(((IfSqlComponent) item).getIfSqlNode()));
                }
            }
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
     * Gets set node list.
     * @return set node list
     */
    public List<UpdateIfSetNode> getSetNodeList() {
        return setNodeList;
    }

    /**
     * Sets parameter type.
     * @param parameterType the parameter type
     */
    public void setParameterType(Class parameterType) {
        this.parameterType = parameterType;
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
