package com.madioter.validator.mybatis.parser.sqlparser;

import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.ColumnNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.InsertNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.ValuesNode;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月14日 <br>
 */
public class InsertSqlParser {

    /**
     * insert语句
     */
    private InsertNode insertNode = new InsertNode();

    /**
     * sql片段
     */
    private List<String> fragments = new ArrayList<String>();


    /**
     * select语句解析类
     * @param sql sql语句
     */
    public InsertSqlParser(String sql) {
        /*对复杂的sql语句进行拆分，拆分方式：对括号进行出入栈处理
            原理：
               1、先按左括号进行拆分字符串
               2、从最后一项开始匹配，找到第一个右括号，将字符串提取出来编入fragments列表，并使用@frg#No@的形式替换当前字符串
               3、一直将所有的括号结构都拆分完全后，进行select...from 结构的匹配，如果存在select...from结构，认为是一条select语句，否则只是语句的一个片段
               4、将语句中的@frg#No@变量替换回来，生成单条sql语句，并编入sql语句列表selectNodeList

               insert语句一共两套结构
               INSERT INTO target [(field1[, field2[, ...]])][IN外部数据库] SELECT field1[, field2[, ...]] FROM tableexpression
               INSERT INTO target [(field1[, field2[, ...]])] VALUES (value1[, value2[, ...])
        */

        List<String> strArr = StringUtil.arrayToList(StringUtil.splitWithBlank(sql));
        int i = 0;
        //解析 insert into table 部分
        for (; i < strArr.size(); i++) {
            String str = strArr.get(i);
            if (str.equals(SqlConstant.INSERT) && i < strArr.size() - 2) {
                String tableName = strArr.get(i + 2);
                TableNode tableNode = new TableNode();
                tableNode.setTableName(tableName);
                insertNode.setTableNode(tableNode);
                i = i + 3;
                break;
            }
        }

        //解析 [(field1[, field2[, ...]])] 部分
        StringBuilder columnBuilder = new StringBuilder();
        for (; i < strArr.size(); i++) {
            String str = strArr.get(i);
            if (str.equals(SqlConstant.VALUES) || str.equals(SqlConstant.SELECT)) {
                break;
            } else {
                columnBuilder.append(str).append(SymbolConstant.SYMBOL_BLANK);
            }
        }
        String columnStr = columnBuilder.toString();
        if (!StringUtil.isBlank(columnStr)) {
            columnStr = columnStr.substring(columnStr.indexOf(SymbolConstant.SYMBOL_LEFT_BRACKET) + 1,
                    columnStr.lastIndexOf(SymbolConstant.SYMBOL_RIGHT_BRACKET));
            List<String> columns = StringUtil.arrayToList(StringUtil.splitWithBlank(columnStr));
            ColumnNode columnNode = new ColumnNode(columns);
            insertNode.setColumnNode(columnNode);
        }

        //解析 SELECT field1[, field2[, ...]] FROM tableexpression
        if (strArr.get(i).equals(SqlConstant.SELECT)) {
            StringBuilder simpleSql = new StringBuilder();
            for (; i < strArr.size(); i++) {
                simpleSql.append(strArr.get(i)).append(SymbolConstant.SYMBOL_BLANK);
            }
            SelectSqlParser sqlParser = new SelectSqlParser(simpleSql.toString());
            insertNode.setSqlNodeList(sqlParser.getSelectNodeList());
        } else if (strArr.get(i).equals(SqlConstant.VALUES)) {
            //解析VALUES (value1[, value2[, ...])
            ValuesNode valueNode = new ValuesNode(strArr.subList(i + 1, strArr.size()));
            insertNode.setValueNode(valueNode);
        }
    }

    /**
     * Gets insert node.
     * @return the insert node
     */
    public InsertNode getInsertNode() {
        return insertNode;
    }

}
