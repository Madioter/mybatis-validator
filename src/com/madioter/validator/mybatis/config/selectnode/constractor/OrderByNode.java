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
public class OrderByNode {

    /**
     * 最细粒度节点
     */
    private List<SelectElement> selectElementList = new ArrayList<SelectElement>();

    /**
     * Instantiates a new Order by node.
     *
     * @param otherText the other text
     */
    public OrderByNode(List<String> otherText) {
        boolean orderFlag = false;
        boolean groupFlag = false;
        OrderNode orderNode = null;
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
                if (orderNode == null && !temp.toLowerCase().trim().equals(SqlConstant.ASC) && !temp.toLowerCase().trim().equals(SqlConstant.DESC)) {
                    orderNode = new OrderNode();
                    orderNode.setOrderColumn(temp);
                    selectElementList.add(orderNode);
                } else if (temp.toLowerCase().trim().equals(SqlConstant.ASC)) {
                    orderNode.setOrderType(OrderNode.OrderType.ASC);
                } else if (temp.toLowerCase().trim().equals(SqlConstant.DESC)) {
                    orderNode.setOrderType(OrderNode.OrderType.DESC);
                }
            } else if (groupFlag) {
                continue;
            }
        }
    }
}
