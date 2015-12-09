package com.madioter.validator.mybatis.parser.statementparser.update;

import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.parser.sqlparser.SqlSourceParser;
import com.madioter.validator.mybatis.model.mybatis.SqlSourceVo;
import com.madioter.validator.mybatis.config.statement.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.exception.ConfigException;
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
public class BaseUpdateStatementParser implements UpdateStatementParser {

    /**
     * 对象引用
     */
    private UpdateMappedStatementItem statementItem;

    @Override
    public void parser(MappedStatement mappedStatement, UpdateMappedStatementItem upateStatementItem) throws ConfigException {
        this.statementItem = upateStatementItem;
        SqlSource sqlSource = mappedStatement.getSqlSource();
        SqlSourceVo sqlSourceVo = SqlSourceParser.parser(sqlSource);
        String sql = sqlSourceVo.getSql();
        List<String> fragment = StringUtil.arrayToList(StringUtil.splitWithBlank(sql));
        boolean whereFlag = false;
        StringBuilder whereText = new StringBuilder();
        for (int i = 0; i < fragment.size(); i++) {
            if (fragment.get(i).equals(SqlConstant.UPDATE) && i < fragment.size() - 1) {
                TableNode tableNode = new TableNode();
                tableNode.setTableName(fragment.get(i + 1));
                statementItem.addTableNode(tableNode);
            } else if (fragment.get(i).equals("where")) {
                whereFlag = true;
            }
            if (whereFlag) {
                whereText.append(fragment.get(i));
            }
        }
        statementItem.setSetNodeList(sqlSourceVo.getSetNodeList());
        statementItem.setSetSqlNode(sqlSourceVo.getSetSqlNode());
        statementItem.setWhereCondition(whereText.toString());
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
}
