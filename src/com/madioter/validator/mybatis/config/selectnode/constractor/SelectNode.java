package com.madioter.validator.mybatis.config.selectnode.constractor;

import com.madioter.validator.mybatis.config.selectnode.SelectElement;
import com.madioter.validator.mybatis.util.SelectTextClassification;
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
 * @taskId  <br>
 * @CreateDate 2015年11月28日 <br>
 */
public class SelectNode {

    /**
     * union结构
     */
    private List<SelectNode> unionSelects;

    /**
     * 保留的sql语句
     */
    private String sql;

    /**
     * 输出字段部分
     */
    private ColumnNode columnNode;

    /**
     * 查询表部分
     */
    private FromNode fromNode;

    /**
     * 条件部分
     */
    private WhereNode whereNode;

    /**
     * 分组部分
     */
    private GroupByNode groupByNode;

    /**
     * 排序部分
     */
    private OrderByNode orderByNode;

    /**
     * 分页部分
     */
    private LimitNode limitNode;

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
     * limit字符片段组
     */
    private List<String> limitText = new ArrayList<String>();

    /**
     * 字符串开始标记
     */
    private SelectTextClassification classification = SelectTextClassification.NULL;

    /**
     * Instantiates a new Select node.
     *
     * @param simpleSelect the simple select
     */
    public SelectNode(String simpleSelect) {
        this.sql = simpleSelect;
        /*解析单句sql语句的结构
            原理：
                1、如果存在union或union all字符，将当前的sql单句做进一步拆分
                2、如果不存在，则执行语句结构划分

            目前未解决的语句:
            SELECT 1 FROM (SELECT 18884 AS vendor_id
                    UNION
                    SELECT 8594 AS vendor_id
                    UNION
                    SELECT 25390 AS vendor_id
                    UNION
                    SELECT 25253 AS vendor_id) a
                    解决方案：必须具备完整的select...from结构才被认为是单句
         */
        String[] selectItems = simpleSelect.split("(\\s+union\\s+|\\s+union\\s+all\\s+)");
        String lastSeq = null;
        StringBuilder selectSeq = new StringBuilder();
        if (selectItems.length > 1) {
            unionSelects = new ArrayList<SelectNode>();
            for (int i = 0; i < selectItems.length; i++) {
                selectSeq.append(selectItems[i]).append(SymbolConstant.SYMBOL_BLANK);
                List<String> stringList = StringUtil.arrayToList(StringUtil.splitWithBlank(selectSeq.toString()));
                if (stringList.contains(SqlConstant.SELECT) && stringList.contains(SqlConstant.FROM)) {
                    if (lastSeq != null) {
                        unionSelects.add(new SelectNode(lastSeq));
                    }
                    lastSeq = selectSeq.toString();
                    selectSeq = new StringBuilder();
                }
            }
            if (selectSeq.toString().length() > 0) {
                if (unionSelects.isEmpty()) {
                    classify(simpleSelect);
                } else if (lastSeq != null) {
                    selectSeq.insert(0, lastSeq + SymbolConstant.SYMBOL_BLANK);
                    unionSelects.add(new SelectNode(selectSeq.toString()));
                } else {
                    unionSelects.add(new SelectNode(selectSeq.toString()));
                }
            }
        } else {
            classify(simpleSelect);
        }
    }

    /**
     * 语句结构分类
     * @param simpleSelect select单句
     */
    private void classify(String simpleSelect) {
        //语句结构分类
        textClassify(simpleSelect);
        columnNode = new ColumnNode(columnText);
        fromNode = new FromNode(tableText);
        whereNode = new WhereNode(whereText);
        groupByNode = new GroupByNode(otherText);
        orderByNode = new OrderByNode(otherText);
        limitNode = new LimitNode(limitText);
    }

    /**
     * 字符串分类
     * @param text 字符串
     */
    private void textClassify(String text) {
        /**
         * 由于都是单句，所以不存在select嵌套的情况
         * select字样出现到from字符出现的间隔内，都为查询结构字符串
         * from字符串到where字符串间隔内的字符串，都为表字符串
         * where字符串到group by或order by字符串，都为条件字符串
         * 剩余字符串都为其他字符串
         */
        String[] textArr = StringUtil.splitWithBlank(text);
        for (int k = 0; k < textArr.length; k++) {
            if (textArr[k].toLowerCase().equals(SqlConstant.SELECT) && classification == SelectTextClassification.NULL) {
                classification = SelectTextClassification.COLUMN;
            } else if (textArr[k].toLowerCase().equals(SqlConstant.FROM) && classification == SelectTextClassification.COLUMN) {
                classification = SelectTextClassification.FROM;
            } else if (textArr[k].toLowerCase().equals(SqlConstant.WHERE) && classification == SelectTextClassification.FROM) {
                classification = SelectTextClassification.WHERE;
            } else if ((textArr[k].toLowerCase().equals(SqlConstant.ORDER) || textArr[k].toLowerCase().equals(SqlConstant.GROUP))
                    && k < textArr.length - 1 && textArr[k + 1].toLowerCase().equals(SqlConstant.BY)
                    && (classification == SelectTextClassification.WHERE || classification == SelectTextClassification.FROM)) {
                classification = SelectTextClassification.OTHER;
                otherText.add(textArr[k]);
            } else if (textArr[k].toLowerCase().equals(SqlConstant.LIMIT) && (classification == SelectTextClassification.WHERE
                    || classification == SelectTextClassification.FROM || classification == SelectTextClassification.OTHER)) {
                classification = SelectTextClassification.LIMIT;
            } else {
                if (classification == SelectTextClassification.COLUMN) {
                    columnText.add(textArr[k]);
                } else if (classification == SelectTextClassification.FROM) {
                    tableText.add(textArr[k]);
                } else if (classification == SelectTextClassification.WHERE) {
                    whereText.add(textArr[k]);
                } else if (classification == SelectTextClassification.OTHER) {
                    otherText.add(textArr[k]);
                } else if (classification == SelectTextClassification.LIMIT) {
                    limitText.add(textArr[k]);
                }
            }
        }
    }

    /**
     * Gets column node.
     * @return column node
     */
    public ColumnNode getColumnNode() {
        return columnNode;
    }

    /**
     * Sets column node.
     * @param columnNode the column node
     */
    public void setColumnNode(ColumnNode columnNode) {
        this.columnNode = columnNode;
    }

    /**
     * Gets from node.
     * @return the from node
     */
    public FromNode getFromNode() {
        return fromNode;
    }

    /**
     * Sets from node.
     * @param fromNode the from node
     */
    public void setFromNode(FromNode fromNode) {
        this.fromNode = fromNode;
    }

    /**
     * Gets where node.
     * @return the where node
     */
    public WhereNode getWhereNode() {
        return whereNode;
    }

    /**
     * Sets where node.
     * @param whereNode the where node
     */
    public void setWhereNode(WhereNode whereNode) {
        this.whereNode = whereNode;
    }

    /**
     * Gets group by node.
     * @return the group by node
     */
    public GroupByNode getGroupByNode() {
        return groupByNode;
    }

    /**
     * Sets group by node.
     * @param groupByNode the group by node
     */
    public void setGroupByNode(GroupByNode groupByNode) {
        this.groupByNode = groupByNode;
    }

    /**
     * Gets order by node.
     * @return the order by node
     */
    public OrderByNode getOrderByNode() {
        return orderByNode;
    }

    /**
     * Sets order by node.
     * @param orderByNode the order by node
     */
    public void setOrderByNode(OrderByNode orderByNode) {
        this.orderByNode = orderByNode;
    }

    /**
     * 获取内部的所有元素
     *
     * @author wangyi8 * @taskId *
     * @return List<SelectElement> list
     */
    public List<SelectElement> selectElements() {
        List<SelectElement> selectElementList = new ArrayList<SelectElement>();
        selectElementList.addAll(columnNode.getSelectElementList());
        selectElementList.addAll(fromNode.getSelectElementList());
        selectElementList.addAll(whereNode.getSelectElementList());
        selectElementList.addAll(orderByNode.getSelectElementList());
        selectElementList.addAll(groupByNode.getSelectElementList());
        selectElementList.add(limitNode);
        return selectElementList;
    }

    /**
     * Gets limit node.
     * @return limit node
     */
    public LimitNode getLimitNode() {
        return limitNode;
    }
}
