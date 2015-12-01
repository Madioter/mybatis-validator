package com.madioter.validator.mybatis.config.statement.insert;

import com.madioter.validator.mybatis.config.statement.InsertMappedStatementItem;
import com.madioter.validator.mybatis.config.tagnode.InsertIfColumnNode;
import com.madioter.validator.mybatis.config.tagnode.InsertIfValueNode;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.ReflectHelper;
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
 * @CreateDate 2015年11月21日 <br>
 */
public class BaseInsertStatementParser implements InsertStatementParser {

    /**
     * Mybatis解析SqlNode的contents属性名
     */
    private static final String CONTENTS = "contents";

    /**
     * SQL中的insert关键字
     */
    private static final String INSERT = "insert";

    /**
     * TrimSqlNode
     */
    private static final String TRIM_SQL_NODE = "TrimSqlNode";

    /**
     * IfSqlNode
     */
    private static final String IF_SQL_NODE = "IfSqlNode";

    /**
     * 对象引用
     */
    private InsertMappedStatementItem statementItem;

    @Override
    public void parser(MappedStatement mappedStatement, InsertMappedStatementItem insertMappedStatementItem) throws ConfigException {
        this.statementItem = insertMappedStatementItem;
        SqlSource sqlSource = mappedStatement.getSqlSource();
        Object rootSqlNode = ReflectHelper.getPropertyValue(sqlSource, "rootSqlNode");
        List contents = (List) ReflectHelper.getPropertyValue(rootSqlNode, CONTENTS);
        if (!contents.isEmpty()) {
            for (Object node : contents) {
                if (node.getClass().getName().endsWith("TextSqlNode") && statementItem.getTableName() == null) {
                    String text = (String) ReflectHelper.getPropertyValue(node, "text");
                    if (text.toLowerCase().contains(INSERT)) {
                        statementItem.setTableName(text.toLowerCase().replace(INSERT, "").replace("into", "").trim());
                    }
                } else if (node.getClass().getName().endsWith(TRIM_SQL_NODE) && statementItem.getIfColumnNodeList() == null) {
                    createColumnNodeList(node);
                } else if (node.getClass().getName().endsWith(TRIM_SQL_NODE) && statementItem.getIfValueNodeList() == null) {
                    createValueNodeList(node);
                }
            }
        }
    }

    /**
     * 构建表字段节点信息
     *
     * @param node xml节点
     * @throws ConfigException <br>
     */
    private void createColumnNodeList(Object node) throws ConfigException {
        statementItem.setColumnSqlNode(node);
        List<InsertIfColumnNode> ifColumnNodeList = new ArrayList<InsertIfColumnNode>();
        Object contentNode = ReflectHelper.getPropertyValue(node, CONTENTS);
        List<Object> contents = (List) ReflectHelper.getPropertyValue(contentNode, CONTENTS);
        for (Object sqlNode : contents) {
            if (sqlNode.getClass().getName().endsWith(IF_SQL_NODE)) {
                ifColumnNodeList.add(new InsertIfColumnNode(sqlNode));
            }
        }
        statementItem.setIfColumnNodeList(ifColumnNodeList);
    }

    /**
     * 构建表value节点信息
     *
     * @param node xml节点
     * @throws ConfigException <br>
     */
    private void createValueNodeList(Object node) throws ConfigException {
        statementItem.setValueSqlNode(node);
        List<InsertIfValueNode> ifValueNodeList = new ArrayList<InsertIfValueNode>();
        Object contentNode = ReflectHelper.getPropertyValue(node, CONTENTS);
        List<Object> contents = (List) ReflectHelper.getPropertyValue(contentNode, CONTENTS);
        for (Object sqlNode : contents) {
            if (sqlNode.getClass().getName().endsWith(IF_SQL_NODE)) {
                ifValueNodeList.add(new InsertIfValueNode(sqlNode));
            }
        }
        statementItem.setIfValueNodeList(ifValueNodeList);
    }
}
