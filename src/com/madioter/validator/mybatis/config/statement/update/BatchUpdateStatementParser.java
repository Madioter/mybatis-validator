package com.madioter.validator.mybatis.config.statement.update;

import com.madioter.validator.mybatis.config.statement.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.config.tagnode.UpdateIfSetNode;
import com.madioter.validator.mybatis.util.ReflectHelper;
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
     * UPDATE
     */
    private static final String UPDATE = "update";

    /**
     * Contents
     */
    private static final String CONTENTS = "contents";

    /**
     * 异常提示信息：文件名和ID
     */
    private static final String MAPPER_FILE_ID = "文件：%s，ID：%s";

    /**
     * text
     */
    private static final String TEXT = "text";


    /**
     * 对象引用
     */
    private UpdateMappedStatementItem statementItem;


    @Override
    public void parser(MappedStatement mappedStatement, UpdateMappedStatementItem updateMappedStatementItem) throws ConfigException {
        this.statementItem = updateMappedStatementItem;
        SqlSource sqlSource = mappedStatement.getSqlSource();
        Object rootSqlNode = ReflectHelper.getPropertyValue(sqlSource, "rootSqlNode");
        List<Object> nodes = (List) ReflectHelper.getPropertyValue(rootSqlNode, CONTENTS);
        if (!nodes.isEmpty()) {
            for (Object node : nodes) {
                if (node.getClass().getName().endsWith("TextSqlNode") && statementItem.getTableName() == null) {
                    String text = (String) ReflectHelper.getPropertyValue(node, TEXT);
                    if (text.toLowerCase().contains(UPDATE)) {
                        statementItem.setTableName(getTableName(text));
                    }
                } else if (node.getClass().getName().endsWith("ForEachSqlNode")) {
                    parseEachNode(node);
                } else if (node.getClass().getName().endsWith("TextSqlNode")) {
                    String text = (String) ReflectHelper.getPropertyValue(node, TEXT);
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
        Object mixedSqlNode = ReflectHelper.getPropertyValue(node, CONTENTS);
        List<Object> contents = (List) ReflectHelper.getPropertyValue(mixedSqlNode, CONTENTS);
        if (!contents.isEmpty()) {
            for (Object innerNode : contents) {
                if (innerNode.getClass().getName().endsWith("TextSqlNode") && statementItem.getTableName() == null) {
                    String text = (String) ReflectHelper.getPropertyValue(innerNode, TEXT);
                    if (text.toLowerCase().contains(UPDATE)) {
                        statementItem.setTableName(getTableName(text));
                    }
                } else if (innerNode.getClass().getName().endsWith("SetSqlNode")) {
                    createSetNodeList(innerNode);
                } else if (innerNode.getClass().getName().endsWith("TextSqlNode") && statementItem.getTableName() != null) {
                    String text = (String) ReflectHelper.getPropertyValue(innerNode, TEXT);
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
        String tableName = text.toLowerCase().replace(UPDATE, "").trim();
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
        Object contentNode = ReflectHelper.getPropertyValue(node, CONTENTS);
        List<Object> contents = (List) ReflectHelper.getPropertyValue(contentNode, CONTENTS);
        for (Object sqlNode : contents) {
            if (sqlNode.getClass().getName().endsWith("IfSqlNode")) {
                updateIfSetNodeList.add(new UpdateIfSetNode(sqlNode));
            }
        }
        statementItem.setSetNodeList(updateIfSetNodeList);
    }
}
