package com.madioter.validator.mybatis.config.statement.update;

import com.madioter.validator.mybatis.config.statement.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.config.tagnode.InsertIfColumnNode;
import com.madioter.validator.mybatis.config.tagnode.UpdateIfSetNode;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.builder.xml.dynamic.IfSqlNode;
import org.apache.ibatis.builder.xml.dynamic.MixedSqlNode;
import org.apache.ibatis.builder.xml.dynamic.SetSqlNode;
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
public class BaseUpdateStatementParser implements UpdateStatementParser {

    /**
     * UPDATE
     */
    private static final String UPDATE = "update";

    /**
     * Contents
     */
    private static final String  CONTENTS = "contents";

    /**
     * text
     */
    private static final String TEXT = "text";

    /**
     * 对象引用
     */
    private UpdateMappedStatementItem statementItem;

    @Override
    public void parser(MappedStatement mappedStatement, UpdateMappedStatementItem upateStatementItem) throws ConfigException {
        this.statementItem = upateStatementItem;
        SqlSource sqlSource = mappedStatement.getSqlSource();
        MixedSqlNode rootSqlNode = (MixedSqlNode) ReflectHelper.getPropertyValue(sqlSource, "rootSqlNode");
        List<SqlNode> contents = (List) ReflectHelper.getPropertyValue(rootSqlNode, CONTENTS);
        if (!contents.isEmpty()) {
            for (SqlNode node : contents) {
                if (node instanceof TextSqlNode && statementItem.getTableName() == null) {
                    String text = (String) ReflectHelper.getPropertyValue(node, TEXT);
                    if (text.toLowerCase().contains(UPDATE)) {
                        statementItem.setTableName(getTableName(text));
                    }
                } else if (node instanceof SetSqlNode) {
                    createSetNodeList((SetSqlNode) node);
                } else if (node instanceof TextSqlNode && statementItem.getTableName() != null) {
                    String text = (String) ReflectHelper.getPropertyValue(node, TEXT);
                    if (!text.trim().equals("")) {
                        statementItem.setWhereCondition(text.trim());
                    }
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
    private void createSetNodeList(SetSqlNode node) throws ConfigException {
        statementItem.setSetSqlNode(node);
        List<UpdateIfSetNode> updateIfSetNodeList = new ArrayList<UpdateIfSetNode>();
        MixedSqlNode contentNode = (MixedSqlNode) ReflectHelper.getPropertyValue(node, CONTENTS);
        List<SqlNode> contents = (List<SqlNode>) ReflectHelper.getPropertyValue(contentNode, CONTENTS);
        for (SqlNode sqlNode : contents) {
            if (sqlNode instanceof IfSqlNode) {
                updateIfSetNodeList.add(new UpdateIfSetNode((IfSqlNode)sqlNode));
            }
        }
        statementItem.setSetNodeList(updateIfSetNodeList);
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
}
