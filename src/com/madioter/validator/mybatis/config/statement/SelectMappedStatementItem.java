package com.madioter.validator.mybatis.config.statement;

import com.madioter.validator.mybatis.config.selectnode.SelectElement;
import com.madioter.validator.mybatis.config.selectnode.constractor.SelectNode;
import com.madioter.validator.mybatis.config.statement.select.BaseSelectStatementParser;
import com.madioter.validator.mybatis.config.statement.select.SelectStatementParser;
import com.madioter.validator.mybatis.config.statement.update.BaseUpdateStatementParser;
import com.madioter.validator.mybatis.config.statement.update.BatchUpdateStatementParser;
import com.madioter.validator.mybatis.config.statement.update.UpdateStatementParser;
import com.madioter.validator.mybatis.config.tagnode.SelectIfNode;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.ClassModel;
import com.madioter.validator.mybatis.util.exception.ConfigException;
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
     * 原数据
     */
    private MappedStatement mappedStatement;

    /**
     * 返回对象
     */
    private ClassModel classModel;

    /**
     * 保存对象类型
     */
    private Class parameterType;

    /**
     * if条件
     */
    private List<SelectIfNode> ifConditions;

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
        this.mappedStatement = mappedStatement;
        this.parameterType = mappedStatement.getParameterMap().getType();
        SelectStatementParser selectStatementParser = null;
        if (parameterType != null && parameterType.equals(List.class)) {
            selectStatementParser = new BaseSelectStatementParser();
        } else {
            selectStatementParser = new BaseSelectStatementParser();
        }
        if (selectStatementParser != null) {
            selectStatementParser.parser(mappedStatement, SelectMappedStatementItem.this);
        }
    }


    @Override
    public void validate(ConnectionManager connectionManager) throws ConfigException {

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
     * Gets class model.
     * @return the class model
     */
    public ClassModel getClassModel() {
        return classModel;
    }

    /**
     * Sets class model.
     * @param classModel the class model
     */
    public void setClassModel(ClassModel classModel) {
        this.classModel = classModel;
    }

    /**
     * Gets if conditions.
     * @return if conditions
     */
    public List<SelectIfNode> getIfConditions() {
        return ifConditions;
    }

    /**
     * Sets if conditions.
     * @param ifConditions the if conditions
     */
    public void setIfConditions(List<SelectIfNode> ifConditions) {
        this.ifConditions = ifConditions;
    }

    /**
     * Sets select node list.
     * @param selectNodeList the select node list
     */
    public void setSelectNodeList(List<SelectNode> selectNodeList) {
        this.selectNodeList = selectNodeList;
    }

    /**
     * Gets select node list.
     * @return the select node list
     */
    public List<SelectNode> getSelectNodeList() {
        return selectNodeList;
    }
}
