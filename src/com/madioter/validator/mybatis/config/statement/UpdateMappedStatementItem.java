package com.madioter.validator.mybatis.config.statement;

import com.madioter.validator.mybatis.config.statement.update.BaseUpdateStatementParser;
import com.madioter.validator.mybatis.config.statement.update.BatchUpdateStatementParser;
import com.madioter.validator.mybatis.config.statement.update.UpdateStatementParser;
import com.madioter.validator.mybatis.config.tagnode.UpdateIfSetNode;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.database.TableDao;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月20日 <br>
 */
public class UpdateMappedStatementItem extends MappedStatementItem {

    /**
     * 异常提示信息：文件名和ID
     */
    private static final String MAPPER_FILE_ID = "文件：%s，ID：%s";

    /**
     * 表名
     */
    private static final String TABLE_NAME = " 表名：%s";

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
     * SET中的sql节点
     */
    private Object setSqlNode;

    /**
     * set中if节点对象列表
     */
    private List<UpdateIfSetNode> setNodeList;

    /**
     * where条件字符串
     */
    private String whereCondition;

    /**
     * 构造方法
     * @param mappedStatement 原数据
     * @throws ConfigException 配置异常
     */
    public UpdateMappedStatementItem(MappedStatement mappedStatement) throws ConfigException {
        this.mappedStatement = mappedStatement;
        this.parameterType = mappedStatement.getParameterMap().getType();
        UpdateStatementParser updateStatementParser = null;
        if (parameterType != null && parameterType.equals(List.class)) {
            updateStatementParser = new BatchUpdateStatementParser();
        } else {
            updateStatementParser = new BaseUpdateStatementParser();
        }
        if (updateStatementParser != null) {
            updateStatementParser.parser(mappedStatement, UpdateMappedStatementItem.this);
        }
    }


    @Override
    public void validate(ConnectionManager connectionManager) throws ConfigException {
        TableDao tableDao = connectionManager.getTableDao();
        ColumnDao columnDao = connectionManager.getColumnDao();
        boolean exist = false;
        if (tableName != null) {
            exist = tableDao.checkExist(tableName);
        }
        if (!exist) {
            new MapperException(ExceptionCommonConstant.TABLE_NOT_EXIST,
                    String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId()) +
                            String.format(TABLE_NAME, tableName)).printException();
            return;
        }
        if (this.setNodeList != null) {
            int len = setNodeList.size();
            //验证字段是否存在
            for (int i = 0; i < len; i++) {
                UpdateIfSetNode node = setNodeList.get(i);
                //验证字段是否存在
                try {
                    node.validateColumn(columnDao, tableName);
                } catch (MapperException e) {
                    e.setDescription(String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())
                            + SymbolConstant.SYMBOL_COLON + e.getDescription());
                    e.printException();
                }
            }
            //判断参数类型
            if (parameterType == null) {
                new MapperException(ExceptionCommonConstant.NO_PROPERTY_VALIDATE_ERROR,
                        String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())).printException();
                return;
            } else if (parameterType.equals(Map.class)) {
                new MapperException(ExceptionCommonConstant.MAP_PROPERTY_VALIDATE_ERROR,
                        String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())).printException();
                return;
            }
            //验证参数中的属性是否存在
            for (int i = 0; i < len; i++) {
                UpdateIfSetNode node = setNodeList.get(i);
                //验证字段和属性是否存在
                try {
                    node.validateProperty(parameterType);
                } catch (MapperException e) {
                    e.setDescription(String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())
                            + SymbolConstant.SYMBOL_COLON + e.getDescription());
                    e.printException();
                }
            }
        }
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

    /**
     * Sets set node list.
     * @param setNodeList the set node list
     */
    public void setSetNodeList(List<UpdateIfSetNode> setNodeList) {
        this.setNodeList = setNodeList;
    }

    /**
     * Gets set node list.
     * @return set node list
     */
    public List<UpdateIfSetNode> getSetNodeList() {
        return setNodeList;
    }

    /**
     * Gets where condition.
     * @return where condition
     */
    public String getWhereCondition() {
        return whereCondition;
    }

    /**
     * Sets where condition.
     * @param whereCondition the where condition
     */
    public void setWhereCondition(String whereCondition) {
        this.whereCondition = whereCondition;
    }
}
