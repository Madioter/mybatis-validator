package com.madioter.validator.mybatis.parser.statementparser.select;

import com.madioter.validator.mybatis.parser.sqlparser.SqlSourceParser;
import com.madioter.validator.mybatis.model.mybatis.SqlSourceVo;
import com.madioter.validator.mybatis.config.statement.SelectMappedStatementItem;
import com.madioter.validator.mybatis.parser.sqlparser.SelectSqlParser;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <Description> SELECT语句解析 <br>
 * 注意：目前无法解析union 和 union all的语句，后面在做完善
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月25日 <br>
 */
public class BaseSelectStatementParser implements SelectStatementParser {

    /**
     * 对象引用
     */
    private SelectMappedStatementItem statementItem;

    /**
     * 语句字符碎片
     */
    private List<String> fragments = new ArrayList<String>();

    /**
     * 解析方法
     *
     * @param mappedStatement 元数据
     * @param selectStatementItem
     * @throws ConfigException
     */
    @Override
    public void parser(MappedStatement mappedStatement, SelectMappedStatementItem selectStatementItem) throws ConfigException {
        this.statementItem = selectStatementItem;

        //解析mybatis的配置，并将select标签解析为最长sql语句
        SqlSource sqlSource = mappedStatement.getSqlSource();
        SqlSourceVo sqlSourceVo = SqlSourceParser.parser(sqlSource);


        //使用select语句的sql解析器进行sql解析
        SelectSqlParser selectSqlParser = new SelectSqlParser(sqlSourceVo.getSql());
        selectStatementItem.setSelectNodeList(selectSqlParser.getSelectNodeList());

        selectStatementItem.setSqlComponentList(sqlSourceVo.getComponentList());
        //selectStatementItem.setIfConditions(sqlSourceVo.getSelectIfNodeList());
    }


}
