package com.madioter.validator.mybatis.config.statement.insert;

import com.madioter.validator.mybatis.config.statement.InsertMappedStatementItem;
import com.madioter.validator.mybatis.config.tagnode.InsertIfColumnNode;
import com.madioter.validator.mybatis.config.tagnode.InsertIfValueNode;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.ReflectHelper;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.builder.xml.dynamic.IfSqlNode;
import org.apache.ibatis.builder.xml.dynamic.MixedSqlNode;
import org.apache.ibatis.builder.xml.dynamic.SqlNode;
import org.apache.ibatis.builder.xml.dynamic.TextSqlNode;
import org.apache.ibatis.builder.xml.dynamic.TrimSqlNode;
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
     * 对象引用
     */
    private InsertMappedStatementItem statementItem;

    @Override
    public void parser(MappedStatement mappedStatement, InsertMappedStatementItem insertMappedStatementItem) throws ConfigException {
        this.statementItem = insertMappedStatementItem;
        SqlSource sqlSource = mappedStatement.getSqlSource();
        MixedSqlNode rootSqlNode = (MixedSqlNode) ReflectHelper.getPropertyValue(sqlSource, "rootSqlNode");
        List<SqlNode> contents = (List) ReflectHelper.getPropertyValue(rootSqlNode, CONTENTS);
        if (!contents.isEmpty()) {
            for (SqlNode node : contents) {
                if (node instanceof TextSqlNode && statementItem.getTableName() == null) {
                    String text = (String) ReflectHelper.getPropertyValue(node, "text");
                    if (text.toLowerCase().contains(INSERT)) {
                        statementItem.setTableName(text.toLowerCase().replace(INSERT, "").replace("into", "").trim());
                    }
                } else if (node instanceof TrimSqlNode && statementItem.getIfColumnNodeList() == null) {
                    createColumnNodeList((TrimSqlNode) node);
                } else if (node instanceof TrimSqlNode && statementItem.getIfValueNodeList() == null) {
                    createValueNodeList((TrimSqlNode) node);
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
    private void createColumnNodeList(TrimSqlNode node) throws ConfigException {
        statementItem.setColumnSqlNode(node);
        List<InsertIfColumnNode> ifColumnNodeList = new ArrayList<InsertIfColumnNode>();
        MixedSqlNode contentNode = (MixedSqlNode) ReflectHelper.getPropertyValue(node, CONTENTS);
        List<SqlNode> contents = (List<SqlNode>) ReflectHelper.getPropertyValue(contentNode, CONTENTS);
        for (SqlNode sqlNode : contents) {
            if (sqlNode instanceof IfSqlNode) {
                ifColumnNodeList.add(new InsertIfColumnNode((IfSqlNode)sqlNode));
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
    private void createValueNodeList(TrimSqlNode node) throws ConfigException {
        statementItem.setValueSqlNode(node);
        List<InsertIfValueNode> ifValueNodeList = new ArrayList<InsertIfValueNode>();
        MixedSqlNode contentNode = (MixedSqlNode) ReflectHelper.getPropertyValue(node, CONTENTS);
        List<SqlNode> contents = (List<SqlNode>) ReflectHelper.getPropertyValue(contentNode, CONTENTS);
        for (SqlNode sqlNode : contents) {
            if (sqlNode instanceof IfSqlNode) {
                ifValueNodeList.add(new InsertIfValueNode((IfSqlNode)sqlNode));
            }
        }
        statementItem.setIfValueNodeList(ifValueNodeList);
    }
}
