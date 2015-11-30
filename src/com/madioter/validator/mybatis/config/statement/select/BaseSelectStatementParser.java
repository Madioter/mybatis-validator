package com.madioter.validator.mybatis.config.statement.select;

import com.madioter.validator.mybatis.config.statement.SelectMappedStatementItem;
import com.madioter.validator.mybatis.config.tagnode.ForEachNode;
import com.madioter.validator.mybatis.config.tagnode.SelectIfNode;
import com.madioter.validator.mybatis.sqlparser.SelectSqlParser;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.builder.xml.dynamic.ForEachSqlNode;
import org.apache.ibatis.builder.xml.dynamic.IfSqlNode;
import org.apache.ibatis.builder.xml.dynamic.MixedSqlNode;
import org.apache.ibatis.builder.xml.dynamic.SqlNode;
import org.apache.ibatis.builder.xml.dynamic.TextSqlNode;
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
     * Mybatis解析SqlNode的contents属性名
     */
    private static final String CONTENTS = "contents";

    /**
     * text
     */
    private static final String TEXT = "text";

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
        MixedSqlNode rootSqlNode = (MixedSqlNode) ReflectHelper.getPropertyValue(sqlSource, "rootSqlNode");
        List<SqlNode> contents = (List) ReflectHelper.getPropertyValue(rootSqlNode, CONTENTS);
        List<SelectIfNode> selectIfNodeList = new ArrayList<SelectIfNode>();
        for (int i = 0; i < contents.size(); i++) {
            SqlNode node = contents.get(i);
            if (node instanceof TextSqlNode) {
                String text = (String) ReflectHelper.getPropertyValue(node, TEXT);
                fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(text)));
            } else if (node instanceof ForEachSqlNode) {
                fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(new ForEachNode((ForEachSqlNode) node).toString())));
            } else if (node instanceof MixedSqlNode) {
                convertMixedNode((MixedSqlNode)node);
            } else if (node instanceof IfSqlNode) {
                SelectIfNode selectIfNode = new SelectIfNode((IfSqlNode) node);
                selectIfNodeList.add(selectIfNode);
                String content = selectIfNode.getIfContent();
                String[] contentArr = StringUtil.splitWithBlank(content);
                for (int j = 0; j < contentArr.length; j++) {
                    fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(contentArr[j])));
                }
            }
        }
        //对sql语句进行标准化，使用单个空格进行分割字符串，并且所有字符串改为小写
        StringBuilder standardSql = new StringBuilder();
        for (int i = 0; i < fragments.size(); i++) {
            standardSql.append(StringUtil.toLowerCaseExceptBrace(fragments.get(i))).append(SymbolConstant.SYMBOL_BLANK);
        }
        //使用select语句的sql解析器进行sql解析
        SelectSqlParser selectSqlParser = new SelectSqlParser(standardSql.toString());
        selectStatementItem.setSelectNodeList(selectSqlParser.getSelectNodeList());

        selectStatementItem.setIfConditions(selectIfNodeList);
    }

    /**
     * 解析MixedSqlNode 循环
     * @param node MixedSqlNode节点
     * @throws ConfigException 异常
     */
    private void convertMixedNode(MixedSqlNode node) throws ConfigException {
        List<SqlNode> sqlNodeList = (List<SqlNode>) ReflectHelper.getPropertyValue(node, CONTENTS);
        for (int k = 0; k < sqlNodeList.size(); k++) {
            if (sqlNodeList instanceof TextSqlNode) {
                String text = (String) ReflectHelper.getPropertyValue(sqlNodeList.get(k), TEXT);
                fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(text)));
            } else if (sqlNodeList.get(k) instanceof MixedSqlNode) {
                convertMixedNode((MixedSqlNode)sqlNodeList.get(k));
            }
        }
    }
}
