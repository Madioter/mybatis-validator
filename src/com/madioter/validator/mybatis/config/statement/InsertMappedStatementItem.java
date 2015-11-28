package com.madioter.validator.mybatis.config.statement;

import com.madioter.validator.mybatis.config.statement.insert.BaseInsertStatementParser;
import com.madioter.validator.mybatis.config.statement.insert.BatchInsertStatementParser;
import com.madioter.validator.mybatis.config.statement.insert.InsertStatementParser;
import com.madioter.validator.mybatis.config.tagnode.InsertIfColumnNode;
import com.madioter.validator.mybatis.config.tagnode.IfNode;
import com.madioter.validator.mybatis.config.tagnode.InsertIfValueNode;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.database.TableDao;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.builder.xml.dynamic.TrimSqlNode;
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
     * 原数据
     */
    private MappedStatement mappedStatement;

    /**
     * insert的表名
     */
    private String tableName;

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
    private TrimSqlNode columnSqlNode;

    /**
     * insert的赋值节点
     */
    private List<InsertIfValueNode> ifValueNodeList;

    /**
     * 用于解析insert value赋值的节点
     */
    private TrimSqlNode valueSqlNode;

    /**
     * 构造方法
     * @param mappedStatement 原数据
     * @throws ConfigException 配置异常
     */
    public InsertMappedStatementItem(MappedStatement mappedStatement) throws ConfigException {
        this.mappedStatement = mappedStatement;
        this.parameterType = mappedStatement.getParameterMap().getType();
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
        boolean exist = tableDao.checkExist(tableName);
        if (!exist) {
            new MapperException(ExceptionCommonConstant.TABLE_NOT_EXIST,
                    String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId()) +
                            String.format(TABLE_NAME, tableName)).printException();
            return;
        }
        if (this.columnSqlNode != null || this.valueSqlNode != null) {
            checkTrimNodeProperty(this.columnSqlNode, ifColumnNodeList);
            checkTrimNodeProperty(this.valueSqlNode, ifValueNodeList);

            if (ifColumnNodeList.size() != ifValueNodeList.size()) {
                new MapperException(ExceptionCommonConstant.INSERT_COLUMN_VALUE_ERROR,
                        String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())).printException();
            } else {
                int len = ifColumnNodeList.size();
                for (int i = 0; i < len; i++) {
                    InsertIfColumnNode columnNode = ifColumnNodeList.get(i);
                    InsertIfValueNode valueNode = ifValueNodeList.get(i);
                    if (!StringUtil.replaceBlank(columnNode.getIfTest()).equals(StringUtil.replaceBlank(valueNode.getIfTest()))) {
                        new MapperException(ExceptionCommonConstant.INSERT_COLUMN_VALUE_SAME_TEST_ERROR,
                                String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())
                                        + String.format(IF_TEST_TEXT, columnNode.getIfTest(), valueNode.getIfTest())).printException();
                    }
                }
                if (parameterType.equals(Map.class)) {
                    new MapperException(ExceptionCommonConstant.MAP_PROPERTY_VALIDATE_ERROR,
                            String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())).printException();
                    return;
                }
                for (int i = 0; i < len; i++) {
                    InsertIfColumnNode columnNode = ifColumnNodeList.get(i);
                    InsertIfValueNode valueNode = ifValueNodeList.get(i);
                    //验证字段是否存在
                    try {
                        columnNode.validate(columnDao, tableName);
                    } catch (MapperException e) {
                        e.setDescription(String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())
                                + SymbolConstant.SYMBOL_COLON + e.getDescription());
                        e.printException();
                    }
                    //验证属性是否存在
                    try {
                        valueNode.validate(parameterType);
                    } catch (MapperException e) {
                        e.setDescription(String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())
                                + SymbolConstant.SYMBOL_COLON + e.getDescription());
                        e.printException();
                    }
                }
            }
        }
    }

    /**
     * 检查TrimSqlNode节点属性编写是否正确
     *
     * @param node 判断节点
     * @param ifNodeList 对应的内部节点
     * @return false 存在错误
     * @throws ConfigException 配置异常
     */
    private boolean checkTrimNodeProperty(TrimSqlNode node, List<? extends IfNode> ifNodeList) throws ConfigException {
        //判断prefix属性是否设置
        String prefix = ((String) ReflectHelper.getPropertyValue(node, "prefix")).trim();
        if (!prefix.equals("(") && !prefix.equals("values (")) {
            new MapperException(ExceptionCommonConstant.INSERT_TRIM_PREFIX_ERROR,
                    String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())).printException();
        }

        //判断suffix属性是否设置
        String suffix = (String) ReflectHelper.getPropertyValue(node, "suffix");
        if (!suffix.equals(")")) {
            new MapperException(ExceptionCommonConstant.INSERT_TRIM_SUFFIX_ERROR,
                    String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())).printException();
        }

        //判断suffixesToOverride属性是否设置
        List suffixesToOverrides = (List) ReflectHelper.getPropertyValue(node, "suffixesToOverride");
        if (suffixesToOverrides != null && !suffixesToOverrides.isEmpty()) {
            String suffixesToOverride = (String) suffixesToOverrides.get(0);
            if (!suffixesToOverride.equals(",")) {
                new MapperException(ExceptionCommonConstant.INSERT_TRIM_SUFFIXES_TO_OVERRIDE_ERROR,
                        String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())).printException();
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
     * Gets table name.
     * @return table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets table name.
     * @param tableName the table name
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Sets value sql node.
     * @param valueSqlNode the value sql node
     */
    public void setValueSqlNode(TrimSqlNode valueSqlNode) {
        this.valueSqlNode = valueSqlNode;
    }

    /**
     * Sets column sql node.
     * @param columnSqlNode the column sql node
     */
    public void setColumnSqlNode(TrimSqlNode columnSqlNode) {
        this.columnSqlNode = columnSqlNode;
    }
}
