package com.madioter.validator.mybatis.config.statement;

import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.parser.statementparser.insert.BaseInsertStatementParser;
import com.madioter.validator.mybatis.parser.statementparser.insert.BatchInsertStatementParser;
import com.madioter.validator.mybatis.parser.statementparser.insert.InsertStatementParser;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfColumnNode;
import com.madioter.validator.mybatis.model.sql.sqltag.IfNode;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfValueNode;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.database.TableDao;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
     * 异常提示信息：文件名和ID
     */
    private static final String MAPPER_FILE_ID = "文件：%s，ID：%s";

    /**
     * 表名
     */
    private static final String TABLE_NAME = " 表名：%s";

    /**
     * if标签的test判断条件不一致
     */
    private static final String IF_TEST_TEXT = "COLUMN条件：%s，VALUES条件：%s";

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
    private List<InsertIfColumnNode> ifColumnNodeList;

    /**
     * 用于解析insert字段名的标签
     */
    private Object columnSqlNode;

    /**
     * insert的赋值节点
     */
    private List<InsertIfValueNode> ifValueNodeList;

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
        super.setMappedStatement(mappedStatement);
        this.parameterType = getMappedStatement().getParameterMap().getType();
        InsertStatementParser insertStatementParser;
        if (parameterType != null && !parameterType.equals(List.class)) {
            insertStatementParser = new BaseInsertStatementParser();
        } else {
            insertStatementParser = new BatchInsertStatementParser();
        }
        insertStatementParser.parser(mappedStatement, InsertMappedStatementItem.this);
    }


    /**
     * 执行验证方法
     * @param connectionManager 表查询
     *
     * @throws ConfigException <br>
     */
    @Override
    public void validate(ConnectionManager connectionManager) throws ConfigException {
        TableDao tableDao = connectionManager.getTableDao();
        ColumnDao columnDao = connectionManager.getColumnDao();
        boolean exist = tableDao.checkExist(tableNode.getTableName());
        if (!exist) {
            new MapperException(ExceptionCommonConstant.TABLE_NOT_EXIST,
                    String.format(MAPPER_FILE_ID, getMappedStatement().getResource(), getMappedStatement().getId()) +
                            String.format(TABLE_NAME, tableNode.getTableName())).printException();
            return;
        }
        if (this.columnSqlNode != null || this.valueSqlNode != null) {
            checkTrimNodeProperty(this.columnSqlNode, ifColumnNodeList);
            checkTrimNodeProperty(this.valueSqlNode, ifValueNodeList);

            if (ifColumnNodeList.size() != ifValueNodeList.size()) {
                new MapperException(ExceptionCommonConstant.INSERT_COLUMN_VALUE_ERROR,
                        String.format(MAPPER_FILE_ID, getMappedStatement().getResource(), getMappedStatement().getId())).printException();
            } else {
                int len = ifColumnNodeList.size();
                for (int i = 0; i < len; i++) {
                    InsertIfColumnNode columnNode = ifColumnNodeList.get(i);
                    InsertIfValueNode valueNode = ifValueNodeList.get(i);
                    if (!StringUtil.replaceBlank(columnNode.getIfTest()).equals(StringUtil.replaceBlank(valueNode.getIfTest()))) {
                        new MapperException(ExceptionCommonConstant.INSERT_COLUMN_VALUE_SAME_TEST_ERROR,
                                String.format(MAPPER_FILE_ID, getMappedStatement().getResource(), getMappedStatement().getId())
                                        + String.format(IF_TEST_TEXT, columnNode.getIfTest(), valueNode.getIfTest())).printException();
                    }
                }
                if (parameterType.equals(Map.class)) {
                    new MapperException(ExceptionCommonConstant.MAP_PROPERTY_VALIDATE_ERROR,
                            String.format(MAPPER_FILE_ID, getMappedStatement().getResource(), getMappedStatement().getId())).printException();
                    return;
                }
                for (int i = 0; i < len; i++) {
                    InsertIfColumnNode columnNode = ifColumnNodeList.get(i);
                    InsertIfValueNode valueNode = ifValueNodeList.get(i);
                    //验证字段是否存在
                    try {
                        columnNode.validate(columnDao, tableNode.getTableName(), parameterType);
                    } catch (MapperException e) {
                        e.setDescription(String.format(MAPPER_FILE_ID, getMappedStatement().getResource(), getMappedStatement().getId())
                                + SymbolConstant.SYMBOL_COLON + e.getDescription());
                        e.printException();
                    }
                    //验证属性是否存在
                    try {
                        valueNode.validate(parameterType);
                    } catch (MapperException e) {
                        e.setDescription(String.format(MAPPER_FILE_ID, getMappedStatement().getResource(), getMappedStatement().getId())
                                + SymbolConstant.SYMBOL_COLON + e.getDescription());
                        e.printException();
                    }
                }
            }
        }
    }

    @Override
    public List<TableNode> getTableNodes() {
        List<TableNode> tableNodes = new ArrayList<TableNode>();
        tableNodes.add(tableNode);
        return tableNodes;
    }

    @Override
    public void addTableNode(TableNode tableNode) {
        this.tableNode = tableNode;
    }

    /**
     * 检查TrimSqlNode节点属性编写是否正确
     *
     * @param node 判断节点
     * @param ifNodeList 对应的内部节点
     * @return false 存在错误
     * @throws ConfigException 配置异常
     */
    private boolean checkTrimNodeProperty(Object node, List<? extends IfNode> ifNodeList) throws ConfigException {
        //判断prefix属性是否设置
        String prefix = ((String) ReflectHelper.getPropertyValue(node, "prefix")).trim();
        if (!prefix.equals(SymbolConstant.SYMBOL_LEFT_BRACKET) && !prefix.equals("values (")) {
            new MapperException(ExceptionCommonConstant.INSERT_TRIM_PREFIX_ERROR,
                    String.format(MAPPER_FILE_ID, getMappedStatement().getResource(), getMappedStatement().getId())).printException();
        }

        //判断suffix属性是否设置
        String suffix = (String) ReflectHelper.getPropertyValue(node, "suffix");
        if (!suffix.equals(SymbolConstant.SYMBOL_RIGHT_BRACKET)) {
            new MapperException(ExceptionCommonConstant.INSERT_TRIM_SUFFIX_ERROR,
                    String.format(MAPPER_FILE_ID, getMappedStatement().getResource(), getMappedStatement().getId())).printException();
        }

        //判断suffixesToOverride属性是否设置
        List suffixesToOverrides = (List) ReflectHelper.getPropertyValue(node, "suffixesToOverride");
        if (suffixesToOverrides != null && !suffixesToOverrides.isEmpty()) {
            String suffixesToOverride = (String) suffixesToOverrides.get(0);
            if (!suffixesToOverride.equals(",")) {
                new MapperException(ExceptionCommonConstant.INSERT_TRIM_SUFFIXES_TO_OVERRIDE_ERROR,
                        String.format(MAPPER_FILE_ID, getMappedStatement().getResource(), getMappedStatement().getId())).printException();
            }
        }
        return true;
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
