package com.madioter.validator.mybatis.config.selectnode.constractor;

import com.madioter.validator.mybatis.config.selectnode.GroupNode;
import com.madioter.validator.mybatis.config.selectnode.OrderNode;
import com.madioter.validator.mybatis.config.selectnode.SelectElement;
import com.madioter.validator.mybatis.util.SqlConstant;
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
public class GroupByNode {

    /**
     * 最细粒度节点
     */
    private List<SelectElement> selectElementList = new ArrayList<SelectElement>();

    /**
     * Instantiates a new Group by node.
     *
     * @param otherText the other text
     */
    public GroupByNode(List<String> otherText) {
        boolean orderFlag = false;
        boolean groupFlag = false;
        boolean havingFlag = false;
        GroupNode groupNode = null;
        for (int i = 0; i < otherText.size(); i++) {
            String temp = otherText.get(i);
            if (temp.toLowerCase().trim().equals(SqlConstant.ORDER) && i < otherText.size() - 1
                    && otherText.get(i + 1).toLowerCase().trim().equals(SqlConstant.BY)) {
                orderFlag = true;
                groupFlag = false;
                i = i + 1;
            } else if (temp.toLowerCase().trim().equals(SqlConstant.GROUP) && i < otherText.size() - 1
                    && otherText.get(i + 1).toLowerCase().trim().equals(SqlConstant.BY)) {
                orderFlag = false;
                groupFlag = true;
                i = i + 1;
            } else if (orderFlag) {
                continue;
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
     * 获取解析后的结构
     * @return List<SelectElement>
     */
    public List<SelectElement> getSelectElementList() {
        return selectElementList;
    }
}
