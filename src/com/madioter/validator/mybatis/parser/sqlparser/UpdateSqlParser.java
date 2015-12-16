package com.madioter.validator.mybatis.parser.sqlparser;

import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.FromNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.GroupByNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.LimitNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.OrderByNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.SetNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.UpdateNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.WhereNode;
import com.madioter.validator.mybatis.util.SelectTextClassification;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.SqlHelperConstant;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description>update语句的解析 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月16日 <br>
 */
public class UpdateSqlParser {

    /**
     * 字符串开始标记
     */
    private SelectTextClassification classification = SelectTextClassification.NULL;


    /**
     * UPDATE语句解析结构
     */
    private UpdateNode updateNode = new UpdateNode();

    /**
     * Instantiates a new Update sql parser.
     *
     * @param sql the sql
     */
    public UpdateSqlParser(String sql) {
        String updateSql = "";
        List<String> sqlFragments = bracketFragment(sql);
        updateNode.setSqlFragments(sqlFragments);
        for (int k = 0; k < sqlFragments.size(); k++) {
            String fragment = sqlFragments.get(k);
            //找到主要的update语句
            if (fragment.contains(SqlConstant.UPDATE) && fragment.contains(SqlConstant.SET)) {
                updateSql = fragment;
                break;
            }
        }
        if (StringUtil.isBlank(updateSql)) {
            new MapperException(ExceptionCommonConstant.CAN_NOT_EXPLAIN_ERROR, sql).printException();
            return;
        }

        List<String> strArr = StringUtil.arrayToList(StringUtil.splitWithBlank(updateSql));
        int i = 0;
        //解析 update table 部分
        for (; i < strArr.size(); i++) {
            String str = strArr.get(i);
            if (str.equals(SqlConstant.UPDATE) && i < strArr.size() - 1) {
                String tableName = strArr.get(i + 1);
                TableNode tableNode = new TableNode();
                tableNode.setTableName(tableName);
                updateNode.setTableNode(tableNode);
                i = i + 2;
                break;
            }
        }

        //过滤掉SET标签
        for (; i < strArr.size(); i++) {
            String str = strArr.get(i);
            if (str.equals(SqlConstant.SET)) {
                break;
            }
        }

        //对剩余部分进行字符串拆分
        List<String> setText = new ArrayList<String>();
        List<String> fromText = new ArrayList<String>();
        List<String> whereText = new ArrayList<String>();
        List<String> orderText = new ArrayList<String>();
        List<String> groupText = new ArrayList<String>();
        List<String> limitText = new ArrayList<String>();

        for (; i < strArr.size(); i++) {
            String str = strArr.get(i);
            if (str.toLowerCase().equals(SqlConstant.FROM) && classification == SelectTextClassification.NULL) {
                classification = SelectTextClassification.FROM;
            } else if (str.toLowerCase().equals(SqlConstant.WHERE) &&
                    (classification == SelectTextClassification.FROM || classification == SelectTextClassification.NULL)) {
                classification = SelectTextClassification.WHERE;
            } else if ((str.toLowerCase().equals(SqlConstant.ORDER))
                    && i < strArr.size() - 1 && strArr.get(i + 1).toLowerCase().equals(SqlConstant.BY)) {
                classification = SelectTextClassification.ORDER;
                orderText.add(str);
            } else if ((str.toLowerCase().equals(SqlConstant.GROUP))
                    && i < strArr.size() - 1 && strArr.get(i + 1).toLowerCase().equals(SqlConstant.BY)) {
                classification = SelectTextClassification.ORDER;
                groupText.add(str);
            } else if (str.toLowerCase().equals(SqlConstant.LIMIT)) {
                classification = SelectTextClassification.LIMIT;
            } else {
                if (classification == SelectTextClassification.NULL) {
                    setText.add(str);
                } else if (classification == SelectTextClassification.FROM) {
                    fromText.add(str);
                } else if (classification == SelectTextClassification.WHERE) {
                    whereText.add(str);
                } else if (classification == SelectTextClassification.ORDER) {
                    orderText.add(str);
                } else if (classification == SelectTextClassification.GROUP) {
                    groupText.add(str);
                } else if (classification == SelectTextClassification.LIMIT) {
                    limitText.add(str);
                }
            }
        }

        // 给各部分赋值
        updateNode.setSetNode(new SetNode(setText));
        updateNode.setFromNode(new FromNode(setText));
        updateNode.setWhereNode(new WhereNode(whereText));
        updateNode.setOrderByNode(new OrderByNode(orderText));
        updateNode.setGroupByNode(new GroupByNode(groupText));
        updateNode.setLimitNode(new LimitNode(limitText));
    }

    /**
     * 按括号拆分sql结构
     * @param sql update的sql语句
     * @return 拆分后的语句块
     */
    private static List<String> bracketFragment(String sql) {
        List<String> fragments = new ArrayList<String>();
        String[] strArr = sql.split(SymbolConstant.SYMBOL_BACK_SLASH + SymbolConstant.SYMBOL_LEFT_BRACKET);
        int length = strArr.length;
        String lastTemp = "";
        for (int i = length - 1; i > 0; i--) {
            if (StringUtil.isBlank(lastTemp)) {
                lastTemp = strArr[i];
            } else {
                lastTemp = strArr[i] + lastTemp;
            }
            String temp = lastTemp.substring(0, lastTemp.indexOf(SymbolConstant.SYMBOL_RIGHT_BRACKET));
            if (temp.equals("")) {
                lastTemp = lastTemp.replaceFirst(SymbolConstant.SYMBOL_BACK_SLASH + SymbolConstant.SYMBOL_RIGHT_BRACKET,
                        SqlHelperConstant.FRAGMENT_BLANK_TAG);
            } else {
                fragments.add(temp);
                lastTemp = SqlHelperConstant.FRAGMENT_TAG + (fragments.size() - 1) + SymbolConstant.SYMBOL_AT +
                        lastTemp.substring(temp.length() + 1);
            }
        }
        fragments.add(strArr[0] + lastTemp);

        List<String> sqlPart = new ArrayList<String>();
        for (int i = 0; i < fragments.size(); i++) {
            String temp = fragments.get(i);
            String[] blankArr = StringUtil.splitWithBlank(temp);
            List<String> blankList = StringUtil.arrayToList(blankArr);
            if ((blankList.contains(SqlConstant.SELECT) && blankList.contains(SqlConstant.FROM) ||
                    (blankList.contains(SqlConstant.UPDATE) && blankList.contains(SqlConstant.SET)))) {
                //把相应的变量进行替换
                for (int k = i; k >= 0; k--) {
                    if (temp.contains(SqlHelperConstant.FRAGMENT_TAG + k + SymbolConstant.SYMBOL_AT)) {
                        temp = temp.replace(SqlHelperConstant.FRAGMENT_TAG + k + SymbolConstant.SYMBOL_AT,
                                SymbolConstant.SYMBOL_LEFT_BRACKET + fragments.get(k) + SymbolConstant.SYMBOL_RIGHT_BRACKET);
                    }
                }
                temp = temp.replace(SqlHelperConstant.FRAGMENT_BLANK_TAG, SymbolConstant.SYMBOL_LEFT_BRACKET
                        + SymbolConstant.SYMBOL_RIGHT_BRACKET);
                //解析单句的sql语句结构
                sqlPart.add(temp);
                fragments.remove(i);
                fragments.add(i, SqlHelperConstant.SELECT_TAG + (sqlPart.size() - 1) + SymbolConstant.SYMBOL_AT);
            }
        }
        return sqlPart;
    }


    /**
     * Gets update node.
     * @return the update node
     */
    public UpdateNode getUpdateNode() {
        return updateNode;
    }
}
