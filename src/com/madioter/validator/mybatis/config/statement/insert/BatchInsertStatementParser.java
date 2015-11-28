package com.madioter.validator.mybatis.config.statement.insert;

import com.madioter.validator.mybatis.config.statement.InsertMappedStatementItem;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.NotSupportException;
import java.util.List;
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
public class BatchInsertStatementParser implements InsertStatementParser {

    /**
     * Mybatis解析SqlNode的contents属性名
     */
    private static final String CONTENTS = "contents";

    /**
     * 异常提示信息：文件名和ID
     */
    private static final String MAPPER_FILE_ID = "文件：%s，ID：%s";

    /**
     * 对象引用
     */
    private InsertMappedStatementItem statementItem;

    /**
     * SQL中的insert关键字
     */
    private static final String INSERT = "insert";

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
                        statementItem.setTableName(getTableName(text));
                    }
                }
            }
        }

        //字段和value值未验证，需要进一步进行字符串拆分
        new NotSupportException(String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())).printException();
    }

    /**
     * 从表达式中提取表名
     *
     * @param text 文本
     * @return String
     */
    private String getTableName(String text) {
        String tableName = text.toLowerCase().replace(INSERT, "").replace("into", "").trim();
        String[] items = StringUtil.splitWithBlank(tableName);
        return items[0];
    }
}
