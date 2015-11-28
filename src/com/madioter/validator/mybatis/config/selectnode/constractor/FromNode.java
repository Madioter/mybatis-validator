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
     * @param tableText the table text
     */
    public FromNode(List<String> tableText) {
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
}
