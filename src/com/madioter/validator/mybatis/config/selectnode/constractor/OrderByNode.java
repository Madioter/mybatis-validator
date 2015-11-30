package com.madioter.validator.mybatis.config.selectnode.constractor;

import com.madioter.validator.mybatis.config.selectnode.GroupNode;
import com.madioter.validator.mybatis.config.selectnode.OrderNode;
import com.madioter.validator.mybatis.config.selectnode.SelectElement;
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
        StringBuilder builder = new StringBuilder();
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
                builder.append(SymbolConstant.SYMBOL_BLANK).append(temp);
            } else if (groupFlag) {
                continue;
            }
        }
        if (!builder.toString().isEmpty()) {
            String[] splitWithComma = builder.toString().split(",");
            for (int i = 0; i < splitWithComma.length; i++) {
                String temp = splitWithComma[i];
                if (temp.toLowerCase().trim().contains(SymbolConstant.SYMBOL_BLANK + SqlConstant.ASC)) {
                    orderNode = new OrderNode();
                    orderNode.setOrderColumn(temp.toLowerCase().trim().replace(SymbolConstant.SYMBOL_BLANK + SqlConstant.ASC, ""));
                    orderNode.setOrderType(OrderNode.OrderType.ASC);
                } else if (temp.toLowerCase().trim().contains(SymbolConstant.SYMBOL_BLANK + SqlConstant.DESC)){
                    orderNode = new OrderNode();
                    orderNode.setOrderColumn(temp.toLowerCase().trim().replace(SymbolConstant.SYMBOL_BLANK + SqlConstant.DESC, ""));
                    orderNode.setOrderType(OrderNode.OrderType.DESC);
                } else {
                    orderNode = new OrderNode();
                    orderNode.setOrderColumn(temp.toLowerCase().trim());
                    orderNode.setOrderType(OrderNode.OrderType.ASC);
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
