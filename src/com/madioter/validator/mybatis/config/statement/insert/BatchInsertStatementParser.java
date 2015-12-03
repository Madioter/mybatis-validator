package com.madioter.validator.mybatis.config.statement.insert;

import com.madioter.validator.mybatis.config.parser.SqlSourceParser;
import com.madioter.validator.mybatis.config.parser.SqlSourceVo;
import com.madioter.validator.mybatis.config.statement.InsertMappedStatementItem;
import com.madioter.validator.mybatis.util.MyBatisTagConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.NotSupportException;
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
public class BatchInsertStatementParser implements InsertStatementParser {

    /**
     * 异常提示信息：文件名和ID
     */
    private static final String MAPPER_FILE_ID = "文件：%s，ID：%s";

    /**
     * 对象引用
     */
    private InsertMappedStatementItem statementItem;

    @Override
    public void parser(MappedStatement mappedStatement, InsertMappedStatementItem insertMappedStatementItem) throws ConfigException {
        this.statementItem = insertMappedStatementItem;
        SqlSource sqlSource = mappedStatement.getSqlSource();
        SqlSourceVo sqlSourceVo = SqlSourceParser.parser(sqlSource);
        String sql = sqlSourceVo.getSql();
        List<String> fragment = StringUtil.arrayToList(StringUtil.splitWithBlank(sql));
        for (int i = 0; i < fragment.size(); i++) {
            if (fragment.get(i).equals(SqlConstant.INSERT) && i < fragment.size() - 2) {
                statementItem.setTableName(fragment.get(i + 2));
            }
        }

        /*Object rootSqlNode = ReflectHelper.getPropertyValue(sqlSource, "rootSqlNode");
        List<Object> contents = (List) ReflectHelper.getPropertyValue(rootSqlNode, MyBatisTagConstant.CONTENTS);
        if (!contents.isEmpty()) {
            for (Object node : contents) {
                if (node.getClass().getName().endsWith(MyBatisTagConstant.TEXT_SQL_NODE) && statementItem.getTableName() == null) {
                    String text = (String) ReflectHelper.getPropertyValue(node, MyBatisTagConstant.TEXT);
                    if (text.toLowerCase().contains(SqlConstant.INSERT)) {
                        statementItem.setTableName(getTableName(text));
                    }
                }
            }
        }*/

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
        String tableName = text.toLowerCase().replace(SqlConstant.INSERT, "").replace("into", "").trim();
        String[] items = StringUtil.splitWithBlank(tableName);
        return items[0];
    }
}
