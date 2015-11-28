package com.madioter.validator.mybatis.config.statement.update;

import com.madioter.validator.mybatis.config.statement.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.config.tagnode.UpdateIfSetNode;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.NotSupportException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.builder.xml.dynamic.ForEachSqlNode;
import org.apache.ibatis.builder.xml.dynamic.IfSqlNode;
import org.apache.ibatis.builder.xml.dynamic.MixedSqlNode;
import org.apache.ibatis.builder.xml.dynamic.SetSqlNode;
import org.apache.ibatis.builder.xml.dynamic.SqlNode;
import org.apache.ibatis.builder.xml.dynamic.TextSqlNode;
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
        MixedSqlNode rootSqlNode = (MixedSqlNode) ReflectHelper.getPropertyValue(sqlSource, "rootSqlNode");
        List<SqlNode> nodes = (List) ReflectHelper.getPropertyValue(rootSqlNode, CONTENTS);
        if (!nodes.isEmpty()) {
            for (SqlNode node : nodes) {
                if (node instanceof TextSqlNode && statementItem.getTableName() == null) {
                    String text = (String) ReflectHelper.getPropertyValue(node, TEXT);
                    if (text.toLowerCase().contains(UPDATE)) {
                        statementItem.setTableName(getTableName(text));
                    }
                } else if (node instanceof ForEachSqlNode) {
                    parseEachNode((ForEachSqlNode) node);
                } else if (node instanceof TextSqlNode) {
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
    private void parseEachNode(ForEachSqlNode node) throws ConfigException {
        MixedSqlNode mixedSqlNode = (MixedSqlNode) ReflectHelper.getPropertyValue(node, CONTENTS);
        List<SqlNode> contents = (List) ReflectHelper.getPropertyValue(mixedSqlNode, CONTENTS);
        if (!contents.isEmpty()) {
            for (SqlNode innerNode : contents) {
                if (innerNode instanceof TextSqlNode && statementItem.getTableName() == null) {
                    String text = (String) ReflectHelper.getPropertyValue(innerNode, TEXT);
                    if (text.toLowerCase().contains(UPDATE)) {
                        statementItem.setTableName(getTableName(text));
                    }
                } else if (innerNode instanceof SetSqlNode) {
                    createSetNodeList((SetSqlNode) innerNode);
                } else if (innerNode instanceof TextSqlNode && statementItem.getTableName() != null) {
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
    private void createSetNodeList(SetSqlNode node) throws ConfigException {
        statementItem.setSetSqlNode(node);
        List<UpdateIfSetNode> updateIfSetNodeList = new ArrayList<UpdateIfSetNode>();
        MixedSqlNode contentNode = (MixedSqlNode) ReflectHelper.getPropertyValue(node, CONTENTS);
        List<SqlNode> contents = (List<SqlNode>) ReflectHelper.getPropertyValue(contentNode, CONTENTS);
        for (SqlNode sqlNode : contents) {
            if (sqlNode instanceof IfSqlNode) {
                updateIfSetNodeList.add(new UpdateIfSetNode((IfSqlNode) sqlNode));
            }
        }
        statementItem.setSetNodeList(updateIfSetNodeList);
    }
}
