package com.madioter.validator.mybatis.config.selectnode.constractor;

import com.madioter.validator.mybatis.config.selectnode.SelectElement;
import com.madioter.validator.mybatis.config.selectnode.TableNode;
import com.madioter.validator.mybatis.util.SqlConstant;
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
public class FromNode {

    /**
     * 最细粒度节点
     */
    private List<SelectElement> selectElementList = new ArrayList<SelectElement>();

    /**
     * 关联关系条件
     */
    private WhereNode joinOnNodes;

    /**
     * Instantiates a new From node.
     *
     * 表内容为：
     *  SELECT 1 FROM (SELECT 18884 AS vendor_id
     *      UNION
     *      SELECT 8594 AS vendor_id
     *      UNION
     *      SELECT 25390 AS vendor_id
     *      UNION
     *      SELECT 25253 AS vendor_id) a
     *
     *  解析思路：括号作为最先级别的完整对象
     *
     * @param tableText the table text
     */
    public FromNode(List<String> tableText) {
        rebuildText(tableText);
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
                    joinOn.add(SqlConstant.AND);
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
        if (joinOn.size() > 0) {
            joinOnNodes = new WhereNode(joinOn);
        }
    }

    /**
     * 重新组织拆分结构，保持括号的完整性
     *
     * @param tableText 表字符列表
     */
    private void rebuildText(List<String> tableText) {
        StringBuilder builder = null;
        List<String> textList = new ArrayList<String>();
        for (String text : tableText) {
            if (text.contains(SymbolConstant.SYMBOL_LEFT_BRACKET) && text.contains(SymbolConstant.SYMBOL_RIGHT_BRACKET)) {
                textList.add(text);
            } else if (text.contains(SymbolConstant.SYMBOL_LEFT_BRACKET)) {
                builder = new StringBuilder(text);
            } else if (text.contains(SymbolConstant.SYMBOL_RIGHT_BRACKET)) {
                builder.append(SymbolConstant.SYMBOL_BLANK).append(text);
                textList.add(builder.toString());
                builder = null;
            } else if (builder != null) {
                builder.append(SymbolConstant.SYMBOL_BLANK).append(text);
            } else {
                textList.add(text);
            }
        }
        tableText.clear();
        tableText.addAll(textList);
    }

    /**
     * 获取解析后的结构
     * @return List<SelectElement> select element list
     */
    public List<SelectElement> getSelectElementList() {
        return selectElementList;
    }

    /**
     * Gets join on nodes.
     * @return join on nodes
     */
    public WhereNode getJoinOnNodes() {
        return joinOnNodes;
    }
}
