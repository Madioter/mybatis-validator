package com.madioter.validator.mybatis.parser.statementparser.insert;

import com.madioter.validator.mybatis.parser.sqlparser.SqlSourceParser;
import com.madioter.validator.mybatis.model.mybatis.SqlSourceVo;
import com.madioter.validator.mybatis.config.statement.InsertMappedStatementItem;
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
public class BaseInsertStatementParser implements InsertStatementParser {

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
        statementItem.setIfColumnNodeList(sqlSourceVo.getIfColumnNodes());
        statementItem.setColumnSqlNode(sqlSourceVo.getColumnSqlNode());
        statementItem.setIfValueNodeList(sqlSourceVo.getIfValueNodes());
        statementItem.setValueSqlNode(sqlSourceVo.getValueSqlNode());
    }

}
