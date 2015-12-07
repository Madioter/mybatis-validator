package com.madioter.validator.mybatis.config.statement;

import com.madioter.validator.mybatis.model.sql.elementnode.ConditionNode;
import com.madioter.validator.mybatis.model.sql.elementnode.FunctionNode;
import com.madioter.validator.mybatis.model.sql.elementnode.OrderNode;
import com.madioter.validator.mybatis.model.sql.elementnode.QueryNode;
import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.ColumnNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.FromNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.GroupByNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.LimitNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.OrderByNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.SelectNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.WhereNode;
import com.madioter.validator.mybatis.parser.statementparser.select.BaseSelectStatementParser;
import com.madioter.validator.mybatis.parser.statementparser.select.SelectStatementParser;
import com.madioter.validator.mybatis.model.sql.sqltag.SelectIfNode;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.database.TableDao;
import com.madioter.validator.mybatis.model.java.ClassModel;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * 当前表
     */
    public static final String CURRENT_TABLE = "@currentTable";

    /**
     * 异常提示信息：文件名和ID
     */
    private static final String MAPPER_FILE_ID = "文件：%s，ID：%s";

    /**
     * if标签的test判断条件不一致
     */
    private static final String IF_TEST_TEXT = "COLUMN条件：%s，VALUES条件：%s";

    /**
     * 异常表达式
     */
    private static final String SQL_EXPRESS_TEXT = "表达式: %s";

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
        super.setMappedStatement(mappedStatement);
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
        Map<String, TableNode> aliasTable = new HashMap<String, TableNode>();
        // 从后向前循环，先外层条件，后内层条件，外层的table 别称可以被内层使用
        // TODO 存在情况：
        // select ...from ... where ... AND
        // b.id = (SELECT batch_handle_id FROM wkf_batch_handle_mapping wm WHERE wm.workform_id =#{workformId})
        // 其中batch_handle_id 作为内部结构，可以不写表别称，应当区分内部表和外部表
        if (selectNodeList != null) {
            for (int i = selectNodeList.size() - 1; i >= 0; i--) {
                SelectNode selectNode = selectNodeList.get(i);
                checkFromNode(selectNode, aliasTable, connectionManager);
                checkColumnNode(selectNode, aliasTable, connectionManager);
                checkWhereNode(selectNode, aliasTable, connectionManager);
                checkOrderByNode(selectNode, aliasTable, connectionManager);
                checkGroupByNode(selectNode, aliasTable, connectionManager);
                checkLimitNode(selectNode);
            }
        }
        if (ifConditions != null) {
            for (SelectIfNode selectIfNode : ifConditions) {
                try {
                    selectIfNode.validate(parameterType);
                } catch (MapperException e) {
                    e.setDescription(String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId()) + e.getDescription());
                    e.printException();
                }
            }
        }
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
     * 验证limit节点
     * @param selectNode select查询单句
     */
    private void checkLimitNode(SelectNode selectNode) {
        String errMsg = String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId());
        LimitNode limitNode = selectNode.getLimitNode();
        limitNode.validate(parameterType, errMsg);
    }

    /**
     * 验证group by节点
     * @param selectNode select查询单句
     * @param aliasTable 表键值对
     * @param connectionManager 数据库连接
     */
    private void checkGroupByNode(SelectNode selectNode, Map<String, TableNode> aliasTable, ConnectionManager connectionManager) {
        String errMsg = String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId());
        GroupByNode groupByNode = selectNode.getGroupByNode();
        if (groupByNode.getSelectElementList() != null) {
            for (SelectElement element : groupByNode.getSelectElementList()) {
                if (element instanceof GroupByNode) {
                    ((GroupByNode) element).validate(aliasTable, connectionManager.getColumnDao(), errMsg);
                }
            }
        }
    }

    /**
     * 验证order by节点
     * @param selectNode select查询单句
     * @param aliasTable 表键值对
     * @param connectionManager 数据库连接
     */
    private void checkOrderByNode(SelectNode selectNode, Map<String, TableNode> aliasTable, ConnectionManager connectionManager) {
        String errMsg = String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId());
        OrderByNode orderByNode = selectNode.getOrderByNode();
        if (orderByNode.getSelectElementList() != null) {
            for (SelectElement element : orderByNode.getSelectElementList()) {
                if (element instanceof OrderNode) {
                    ((OrderNode) element).validate(aliasTable, connectionManager.getColumnDao(), errMsg);
                }
            }
        }
    }

    /**
     * 验证where节点
     * @param selectNode select查询单句
     * @param aliasTable 表键值对
     * @param connectionManager 数据库连接
     */
    private void checkWhereNode(SelectNode selectNode, Map<String, TableNode> aliasTable, ConnectionManager connectionManager) {
        String errMsg = String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId());
        WhereNode whereNode = selectNode.getWhereNode();
        if (whereNode.getSelectElementList() != null) {
            for (SelectElement element : whereNode.getSelectElementList()) {
                if (element instanceof ConditionNode) {
                    ((ConditionNode) element).validate(aliasTable, connectionManager.getColumnDao(), parameterType, errMsg);
                }
            }
        }
    }

    /**
     * 验证column节点
     * @param selectNode select查询单句
     * @param aliasTable 表键值对
     * @param connectionManager 数据库连接
     */
    private void checkColumnNode(SelectNode selectNode, Map<String, TableNode> aliasTable, ConnectionManager connectionManager) {
        String errMsg = String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId());
        ColumnNode columnNode = selectNode.getColumnNode();
        if (columnNode.getSelectElementList() != null) {
            for (SelectElement element : columnNode.getSelectElementList()) {
                if (element instanceof QueryNode) {
                    ((QueryNode) element).validate(aliasTable, connectionManager.getColumnDao(), errMsg);
                } else if (element instanceof FunctionNode) {
                    ((FunctionNode) element).validate(aliasTable, connectionManager.getColumnDao(), errMsg);
                }
            }
        }
    }

    /**
     * 验证from节点，并且返回table
     * @param selectNode select查询单句
     * @param aliasTable 表键值对
     * @param connectionManager 数据库连接
     */
    private void checkFromNode(SelectNode selectNode, Map<String, TableNode> aliasTable, ConnectionManager connectionManager) {
        String errMsg = String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId());
        TableDao tableDao = connectionManager.getTableDao();
        ColumnDao columnDao = connectionManager.getColumnDao();
        FromNode fromNode = selectNode.getFromNode();
        WhereNode whereNode = fromNode.getJoinOnNodes();
        TableNode currentTableNode = null;
        if (fromNode.getSelectElementList() != null) {
            for (SelectElement element : fromNode.getSelectElementList()) {
                if (element instanceof TableNode) {
                    TableNode tableNode = (TableNode) element;
                    // 如果当前只有一个TableNode，返回这个tableNode，否则返回null
                    if (currentTableNode == null) {
                        currentTableNode = tableNode;
                    } else if (currentTableNode != null) {
                        currentTableNode = null;
                    }
                    //Map的键默认存在表别称的使用表别称，不存在的使用表名小写（允许表名不存在）
                    if (tableNode.getTableAlias() != null && !tableNode.getTableAlias().equals("")) {
                        aliasTable.put(tableNode.getTableAlias(), tableNode);
                    } else {
                        aliasTable.put(tableNode.getTableName().toLowerCase(), tableNode);
                    }
                }
            }
            //验证表名是否存在
            for (String set : aliasTable.keySet()) {
                TableNode tableNode = aliasTable.get(set);
                tableNode.validate(tableDao, errMsg);
            }
        }
        if (whereNode != null && whereNode.getSelectElementList() != null) {
            for (SelectElement element : whereNode.getSelectElementList()) {
                if (element instanceof ConditionNode) {
                    ConditionNode conditionNode = (ConditionNode) element;
                    conditionNode.validate(aliasTable, columnDao, parameterType, errMsg);
                }
            }
        }
        if (currentTableNode != null) {
            aliasTable.put(CURRENT_TABLE, currentTableNode);
        } else {
            aliasTable.remove(CURRENT_TABLE);
        }
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
