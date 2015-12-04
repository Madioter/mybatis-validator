package com.madioter.validator.mybatis.parser.statementparser.update;

import com.madioter.validator.mybatis.config.statement.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.model.sql.sqltag.UpdateIfSetNode;
import com.madioter.validator.mybatis.util.MyBatisTagConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.NotSupportException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月23日 <br>
 */
public class BatchUpdateStatementParser implements UpdateStatementParser {

    /**
     * 异常提示信息：文件名和ID
     */
    private static final String MAPPER_FILE_ID = "文件：%s，ID：%s";


    /**
     * 对象引用
     */
    private UpdateMappedStatementItem statementItem;


    @Override
    public void parser(MappedStatement mappedStatement, UpdateMappedStatementItem updateMappedStatementItem) throws ConfigException {
        this.statementItem = updateMappedStatementItem;
        SqlSource sqlSource = mappedStatement.getSqlSource();
        Object rootSqlNode = ReflectHelper.getPropertyValue(sqlSource, "rootSqlNode");
        List<Object> nodes = (List) ReflectHelper.getPropertyValue(rootSqlNode, MyBatisTagConstant.CONTENTS);
        if (!nodes.isEmpty()) {
            for (Object node : nodes) {
                if (node.getClass().getName().endsWith(MyBatisTagConstant.TEXT_SQL_NODE) && statementItem.getTableName() == null) {
                    String text = (String) ReflectHelper.getPropertyValue(node, MyBatisTagConstant.TEXT);
                    if (text.toLowerCase().contains(SqlConstant.UPDATE)) {
                        statementItem.setTableName(getTableName(text));
                    }
                } else if (node.getClass().getName().endsWith("ForEachSqlNode")) {
                    parseEachNode(node);
                } else if (node.getClass().getName().endsWith(MyBatisTagConstant.TEXT_SQL_NODE)) {
                    String text = (String) ReflectHelper.getPropertyValue(node, MyBatisTagConstant.TEXT);
                    if (!text.trim().equals("")) {
                        statementItem.setWhereCondition(text.trim());
                    }
                }
            }
        }
        if (statementItem.getTableName() == null || statementItem.getSetNodeList() == null) {
            //字段和value值未验证，需要进一步进行字符串拆分
            new NotSupportException(String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())).printException();
        }
    }

    /**
     * 解析update中的foreach标签
     *
     * @param node 节点
     * @throws ConfigException 配置异常
     */
    private void parseEachNode(Object node) throws ConfigException {
        Object mixedSqlNode = ReflectHelper.getPropertyValue(node, MyBatisTagConstant.CONTENTS);
        List<Object> contents = (List) ReflectHelper.getPropertyValue(mixedSqlNode, MyBatisTagConstant.CONTENTS);
        if (!contents.isEmpty()) {
            for (Object innerNode : contents) {
                if (innerNode.getClass().getName().endsWith(MyBatisTagConstant.TEXT_SQL_NODE) && statementItem.getTableName() == null) {
                    String text = (String) ReflectHelper.getPropertyValue(innerNode, MyBatisTagConstant.TEXT);
                    if (text.toLowerCase().contains(SqlConstant.UPDATE)) {
                        statementItem.setTableName(getTableName(text));
                    }
                } else if (innerNode.getClass().getName().endsWith(MyBatisTagConstant.SET_SQL_NODE)) {
                    createSetNodeList(innerNode);
                } else if (innerNode.getClass().getName().endsWith(MyBatisTagConstant.TEXT_SQL_NODE) && statementItem.getTableName() != null) {
                    String text = (String) ReflectHelper.getPropertyValue(innerNode, MyBatisTagConstant.TEXT);
                    if (!text.trim().equals("")) {
                        statementItem.setWhereCondition(text.trim());
                    }
                }
            }
        }
    }

    /**
     * 从表达式中提取表名
     *
     * @param text 文本
     * @return String
     */
    private String getTableName(String text) {
        String tableName = text.toLowerCase().replace(SqlConstant.UPDATE, "").trim();
        String[] items = StringUtil.splitWithBlank(tableName);
        return items[0];
    }

    /**
     * 构建表字段节点信息
     *
     * @param node xml节点
     * @throws ConfigException <br>
     */
    private void createSetNodeList(Object node) throws ConfigException {
        statementItem.setSetSqlNode(node);
        List<UpdateIfSetNode> updateIfSetNodeList = new ArrayList<UpdateIfSetNode>();
        Object contentNode = ReflectHelper.getPropertyValue(node, MyBatisTagConstant.CONTENTS);
        List<Object> contents = (List) ReflectHelper.getPropertyValue(contentNode, MyBatisTagConstant.CONTENTS);
        for (Object sqlNode : contents) {
            if (sqlNode.getClass().getName().endsWith(MyBatisTagConstant.IF_SQL_NODE)) {
                updateIfSetNodeList.add(new UpdateIfSetNode(sqlNode));
            }
        }
        statementItem.setSetNodeList(updateIfSetNodeList);
    }
}
