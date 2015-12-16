package com.madioter.validator.mybatis.model.sql.sqlnode;

import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.util.SqlHelperConstant;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import java.util.List;

/**
 * <Description> update语句解析 <br>
 *
 *  Mysql的UPDATE语句的四种写法：
 *     1、UPDATE authors
 *           SET state = 'PC', city = 'Bay City'
 *             WHERE state = 'CA' AND city = 'Oakland' ORDER BY id DESC LIMIT 10
 *     2、UPDATE titles
 *           SET ytd_sales = titles.ytd_sales + sales.qty
 *             FROM titles, sales
 *             WHERE titles.title_id = sales.title_id AND sales.ord_date = (SELECT MAX(sales.ord_date) FROM sales)
 *      3、UPDATE titles
 *           SET ytd_sales = (SELECT SUM(qty) FROM sales WHERE sales.title_id = titles.title_id
 *                    AND sales.ord_date IN (SELECT MAX(ord_date) FROM sales))
 *              FROM titles, sales
 *      4、UPDATE authors
 *           SET state = 'ZZ'
 *              FROM (SELECT TOP 10 * FROM authors ORDER BY au_lname) AS t1
 *              WHERE authors.au_id = t1.au_id
 *
 *      update语句的基本构成
 *          UPDATE table SET field = （select）(,field = (conTable.value)) (,field = (default)) FROM conTable WHERE conditions
 *      update语句被拆分成 update，set，from，where 四个部分
 *          set被拆分为field组
 *                 field被设置为对应固定值，sql语句和 表字段值三种情况
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月16日 <br>
 */
public class UpdateNode {

    /**
     * update的主表
     */
    private TableNode tableNode;

    /**
     * set赋值部分
     */
    private SetNode setNode;

    /**
     * from表部分
     */
    private FromNode fromNode;

    /**
     * where条件部分
     */
    private WhereNode whereNode;

    /**
     * order by部分
     */
    private OrderByNode orderByNode;

    /**
     * group by 部分
     */
    private GroupByNode groupByNode;

    /**
     * limit部分
     */
    private LimitNode limitNode;

    /**
     * 保存关联的sql
     */
    private List<String> sqlFragments;


    /**
     * 还原字符结构中的sql语句部分
     * @param fragment 字符表达式
     * @return 还原sql部分后的表达式
     */
    public String getSqlFragment(String fragment) {
        if (fragment.contains(SqlHelperConstant.SELECT_TAG)) {
            List<String> selectItem = StringUtil.matchesRegex(fragment, SqlHelperConstant.SELECT_TAG_REGEX);
            for (int n = 0; n < selectItem.size(); n++) {
                String selectTag = selectItem.get(n);
                String indexStr = selectTag.replace(SqlHelperConstant.SELECT_TAG, "").replace(SymbolConstant.SYMBOL_AT, "");
                int index = Integer.valueOf(indexStr);
                fragment = fragment.replace(selectTag, sqlFragments.get(index));
            }
        }
        return fragment;
    }

    /**
     * Gets table node.
     * @return the table node
     */
    public TableNode getTableNode() {
        return tableNode;
    }

    /**
     * Sets table node.
     * @param tableNode the table node
     */
    public void setTableNode(TableNode tableNode) {
        this.tableNode = tableNode;
    }

    /**
     * Gets set node.
     * @return the set node
     */
    public SetNode getSetNode() {
        return setNode;
    }

    /**
     * Sets set node.
     * @param setNode the set node
     */
    public void setSetNode(SetNode setNode) {
        this.setNode = setNode;
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
     * Gets limit node.
     * @return the limit node
     */
    public LimitNode getLimitNode() {
        return limitNode;
    }

    /**
     * Sets limit node.
     * @param limitNode the limit node
     */
    public void setLimitNode(LimitNode limitNode) {
        this.limitNode = limitNode;
    }

    /**
     * Gets sql fragments.
     * @return the sql fragments
     */
    public List<String> getSqlFragments() {
        return sqlFragments;
    }

    /**
     * Sets sql fragments.
     * @param sqlFragments the sql fragments
     */
    public void setSqlFragments(List<String> sqlFragments) {
        this.sqlFragments = sqlFragments;
    }
}
