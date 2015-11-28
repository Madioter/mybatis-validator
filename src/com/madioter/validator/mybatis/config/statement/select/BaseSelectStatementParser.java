package com.madioter.validator.mybatis.config.statement.select;

import com.madioter.validator.mybatis.config.selectnode.ConditionNode;
import com.madioter.validator.mybatis.config.selectnode.FunctionNode;
import com.madioter.validator.mybatis.config.selectnode.GroupNode;
import com.madioter.validator.mybatis.config.selectnode.OrderNode;
import com.madioter.validator.mybatis.config.selectnode.QueryNode;
import com.madioter.validator.mybatis.config.selectnode.SelectElement;
import com.madioter.validator.mybatis.config.selectnode.TableNode;
import com.madioter.validator.mybatis.config.statement.SelectMappedStatementItem;
import com.madioter.validator.mybatis.config.tagnode.ForEachNode;
import com.madioter.validator.mybatis.config.tagnode.SelectIfNode;
import com.madioter.validator.mybatis.sqlparser.SelectSqlParser;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.SelectTextClassification;
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
     * item
     */
    private static final String ITEM = "item";

    /**
     * and
     */
    private static final String AND = "and";

    /**
     * order
     */
    private static final String ORDER = "order";

    /**
     * group
     */
    private static final String GROUP = "group";

    /**
     * by
     */
    private static final String BY = "by";

    /**
     * asc
     */
    private static final String ASC = "asc";

    /**
     * desc
     */
    private static final String DESC = "desc";

    /**
     * 对象引用
     */
    private SelectMappedStatementItem statementItem;

    /**
     * 查询字符串片段组
     */
    private List<String> columnText = new ArrayList<String>();

    /**
     * 表字符串片段组
     */
    private List<String> tableText = new ArrayList<String>();

    /**
     * where条件字符串片段组
     */
    private List<String> whereText = new ArrayList<String>();

    /**
     * 其他字符串片段组
     */
    private List<String> otherText = new ArrayList<String>();

    /**
     * 字符串开始标记
     */
    private SelectTextClassification classification = SelectTextClassification.NULL;

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
        SqlSource sqlSource = mappedStatement.getSqlSource();
        MixedSqlNode rootSqlNode = (MixedSqlNode) ReflectHelper.getPropertyValue(sqlSource, "rootSqlNode");
        List<SqlNode> contents = (List) ReflectHelper.getPropertyValue(rootSqlNode, CONTENTS);
        List<SelectElement> selectElementList = new ArrayList<SelectElement>();
        List<SelectIfNode> selectIfNodeList = new ArrayList<SelectIfNode>();
        for (int i = 0; i < contents.size(); i++) {
            SqlNode node = contents.get(i);
            if (node instanceof TextSqlNode) {
                String text = (String) ReflectHelper.getPropertyValue(node, TEXT);
                fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(text)));
                //textClassify(text);
            } else if (node instanceof ForEachSqlNode) {
                fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(new ForEachNode((ForEachSqlNode) node).toString())));
                //whereText.add(new ForEachNode((ForEachSqlNode) node).toString());
            } else if (node instanceof MixedSqlNode) {
                List<SqlNode> sqlNodeList = (List<SqlNode>) ReflectHelper.getPropertyValue(node, CONTENTS);
                for (int k = 0; k < sqlNodeList.size(); k++) {
                    String text = (String) ReflectHelper.getPropertyValue(sqlNodeList.get(k), TEXT);
                    fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(text)));
                    //textClassify(text);
                }
            } else if (node instanceof IfSqlNode) {
                SelectIfNode selectIfNode = new SelectIfNode((IfSqlNode) node);
                selectIfNodeList.add(selectIfNode);
                String content = selectIfNode.getIfContent();
                String[] contentArr = StringUtil.splitWithBlank(content);
                for (int j = 0; j < contentArr.length; j++) {
                    fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(contentArr[j])));
                    //whereText.add(contentArr[j]);
                }
            }
        }
        StringBuilder standardSql = new StringBuilder();
        for (int i = 0; i < fragments.size(); i++) {
            standardSql.append(fragments.get(i).toLowerCase()).append(SymbolConstant.SYMBOL_BLANK);
        }
        SelectSqlParser selectSqlParser = new SelectSqlParser(standardSql.toString());
        selectStatementItem.setSelectNodeList(selectSqlParser.getSelectNodeList());


        //createQueryNode(selectElementList);
        //createTableNode(selectElementList);
        //createConditionNode(selectElementList);
        //createOtherNode(selectElementList);
        //selectStatementItem.setElements(selectElementList);
        selectStatementItem.setIfConditions(selectIfNodeList);
    }

    /**
     * 字符串分类
     * @param text 字符串
     */
    private void textClassify(String text) {
        String[] textArr = StringUtil.splitWithBlank(text);
        for (int k = 0; k < textArr.length; k++) {
            if (textArr[k].toLowerCase().equals("select") && classification == SelectTextClassification.NULL) {
                classification = SelectTextClassification.COLUMN;
            } else if (textArr[k].toLowerCase().equals("from") && classification == SelectTextClassification.COLUMN) {
                classification = SelectTextClassification.FROM;
            } else if (textArr[k].toLowerCase().equals("where") && classification == SelectTextClassification.FROM) {
                classification = SelectTextClassification.WHERE;
            } else if ((textArr[k].toLowerCase().equals(ORDER) || textArr[k].toLowerCase().equals(GROUP))
                    && k < textArr.length - 1 && textArr[k + 1].toLowerCase().equals(BY) && classification == SelectTextClassification.WHERE) {
                classification = SelectTextClassification.OTHER;
                otherText.add(textArr[k]);
            } else {
                if (classification == SelectTextClassification.COLUMN) {
                    columnText.add(textArr[k]);
                } else if (classification == SelectTextClassification.FROM) {
                    tableText.add(textArr[k]);
                } else if (classification == SelectTextClassification.WHERE) {
                    whereText.add(textArr[k]);
                } else if (classification == SelectTextClassification.OTHER) {
                    otherText.add(textArr[k]);
                }
            }
        }
    }

    /**
     * select 语句其他部分解析
     *
     * @param selectElementList 结构对象
     */
    private void createOtherNode(List<SelectElement> selectElementList) {
        boolean orderFlag = false;
        boolean groupFlag = false;
        boolean havingFlag = false;
        OrderNode orderNode = null;
        GroupNode groupNode = null;
        for (int i = 0; i < otherText.size(); i++) {
            String temp = otherText.get(i);
            if (temp.toLowerCase().trim().equals(ORDER) && i < otherText.size() - 1
                    && otherText.get(i + 1).toLowerCase().trim().equals(BY)) {
                orderFlag = true;
                groupFlag = false;
                i = i + 1;
            } else if (temp.toLowerCase().trim().equals(GROUP) && i < otherText.size() - 1
                    && otherText.get(i + 1).toLowerCase().trim().equals(BY)) {
                orderFlag = false;
                groupFlag = true;
                i = i + 1;
            } else if (orderFlag) {
                if (orderNode == null && !temp.toLowerCase().trim().equals(ASC) && !temp.toLowerCase().trim().equals(DESC)) {
                    orderNode = new OrderNode();
                    orderNode.setOrderColumn(temp);
                    selectElementList.add(orderNode);
                } else if (temp.toLowerCase().trim().equals(ASC)) {
                    orderNode.setOrderType(OrderNode.OrderType.ASC);
                } else if (temp.toLowerCase().trim().equals(DESC)) {
                    orderNode.setOrderType(OrderNode.OrderType.DESC);
                }
            } else if (groupFlag) {
                groupNode = new GroupNode();
                selectElementList.add(groupNode);
                if (temp.toLowerCase().trim().equals("having")) {
                    havingFlag = true;
                } else if (havingFlag) {
                    groupNode.setHavingConditions(groupNode.getHavingConditions() + temp);
                } else {
                    groupNode.setColumnNames(groupNode.getColumnNames() + temp);
                }
            }
        }
    }

    /**
     * 条件部分字符串解析
     *
     * @param selectElementList 结构对象
     */
    private void createConditionNode(List<SelectElement> selectElementList) {
        ConditionNode lastNode = null;
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < whereText.size(); i++) {
            if (whereText.get(i).toLowerCase().equals(AND) || whereText.get(i).toLowerCase().equals("or") ||
                    whereText.get(i).toLowerCase().equals("left") || whereText.get(i).toLowerCase().equals("right")) {
                if (lastNode == null) {
                    continue;
                }
                lastNode.setValue(value.toString());
                value = new StringBuilder();
                lastNode = null;
            } else if (lastNode == null) {
                lastNode = new ConditionNode();
                selectElementList.add(lastNode);
                lastNode.setColumnName(whereText.get(i));
            } else if (lastNode.getConditionType() == null) {
                lastNode.setConditionType(whereText.get(i));
            } else {
                value.append(whereText.get(i)).append(SymbolConstant.SYMBOL_BLANK);
            }
        }
        if (lastNode != null && value != null) {
            lastNode.setValue(value.toString());
        }
    }

    /**
     * 表部分字符串解析
     *
     * @param selectElementList 结构对象
     */
    private void createTableNode(List<SelectElement> selectElementList) {
        List<String> joinOn = new ArrayList<String>();
        boolean onBegin = false;
        TableNode lastNode = null;
        for (int i = 0; i < tableText.size(); i++) {
            if (tableText.get(i).toLowerCase().equals("dual")) {
                continue;
            } else {
                if (tableText.get(i).toLowerCase().equals("join")) {
                    lastNode = null;
                    onBegin = false;
                } else if (tableText.get(i).contains(SymbolConstant.SYMBOL_COMMA)) {
                    String[] tableNodes = tableText.get(i).split(SymbolConstant.SYMBOL_COMMA);
                    for (int k = 0; k < tableNodes.length; k++) {
                        if (lastNode != null) {
                            lastNode.setTableAlias(tableNodes[k]);
                            lastNode = null;
                        } else {
                            lastNode = new TableNode();
                            lastNode.setTableName(tableNodes[k]);
                            selectElementList.add(lastNode);
                        }
                    }
                } else if (tableText.get(i).toLowerCase().equals("on")) {
                    lastNode = null;
                    joinOn.add(AND);
                    onBegin = true;
                } else {
                    if (lastNode != null) {
                        if (lastNode.getTableAlias() == null) {
                            lastNode.setTableAlias(tableText.get(i));
                        }
                    } else if (onBegin) {
                        joinOn.add(tableText.get(i));
                    } else {
                        lastNode = new TableNode();
                        lastNode.setTableName(tableText.get(i));
                        selectElementList.add(lastNode);
                    }
                }
            }
        }
        whereText.addAll(joinOn);
    }

    /**
     * 查询字段字符串解析
     *
     * @param selectElementList 结构对象
     */
    private void createQueryNode(List<SelectElement> selectElementList) {
        SelectElement lastNode = null;
        for (int i = 0; i < columnText.size(); i++) {
            if (columnText.get(i).toLowerCase().equals("distinct") || columnText.get(i).toLowerCase().equals("as")) {
                continue;
            } else {
                String text = columnText.get(i);

                //逗号结尾解析出来只有单项，不包含逗号后的空字符，这里辅助使用了#进行标记，#作为无别称
                if (columnText.get(i).endsWith(SymbolConstant.SYMBOL_COMMA)) {
                    text = text + SymbolConstant.SYMBOL_NUMBER;
                }
                String[] columnTextNode = text.split(SymbolConstant.SYMBOL_COMMA);

                for (int k = 0; k < columnTextNode.length; k++) {
                    if (k > 0) {
                        lastNode = null;
                    }
                    SelectElement currentNode = buildColumnNode(columnTextNode[k], lastNode);
                    if (currentNode != null && lastNode != null && currentNode == lastNode) {
                        continue;
                    } else {
                        lastNode = currentNode;
                        selectElementList.add(currentNode);
                    }
                }
            }
        }
    }

    /**
     * 构造查询语句节点
     *
     * @param text 查询语句字符串
     * @param lastNode 上次节点
     * @return 当前节点
     */
    private SelectElement buildColumnNode(String text, SelectElement lastNode) {
        if (lastNode != null) {
            if (lastNode instanceof QueryNode) {
                ((QueryNode) lastNode).setColumnAlias(text);
                return null;
            } else if (lastNode instanceof FunctionNode) {
                if (((FunctionNode) lastNode).getExpress().contains(")")) {
                    ((FunctionNode) lastNode).setAlias(text);
                    return null;
                } else {
                    FunctionNode functionNode = (FunctionNode) lastNode;
                    functionNode.setExpress(functionNode.getExpress() + SymbolConstant.SYMBOL_BLANK + text);
                }
            }
        } else if (!text.equals(SymbolConstant.SYMBOL_NUMBER)) {
            if (text.contains("(")) {
                FunctionNode node = new FunctionNode();
                node.setExpress(text);
                return node;
            } else {
                QueryNode node = new QueryNode();
                node.setColumnName(text);
                return node;
            }
        }
        return lastNode;
    }

    /*public static void main(String[] args){
        BaseSelectStatementParser baseSelectStatementParser = new BaseSelectStatementParser();
        String str = "id,t.id,a.id as a, b.id b, c.id ,d.id , now( ), GROUP_CONCAT( DISTINCT m.order_id ) AS order_id";
        String[] strArr = StringUtil.splitWithBlank(str);
        List<String> strList = new ArrayList<String>();
        for (int i = 0; i < strArr.length; i++) {
            strList.add(strArr[i]);
        }
        List<SelectElement> selectElementList = new ArrayList<SelectElement>();
        baseSelectStatementParser.columnText = strList;
        baseSelectStatementParser.createQueryNode(selectElementList);
    }*/
}
