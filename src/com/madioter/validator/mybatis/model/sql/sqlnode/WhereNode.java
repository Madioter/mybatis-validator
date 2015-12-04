package com.madioter.validator.mybatis.model.sql.sqlnode;

import com.madioter.validator.mybatis.model.sql.elementnode.ConditionNode;
import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
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
public class WhereNode {

    /**
     * 最细粒度节点
     */
    private List<SelectElement> selectElementList = new ArrayList<SelectElement>();

    /**
     * 条件节点
     * @param whereText the join on
     */
    public WhereNode(List<String> whereText) {
        ConditionNode lastNode = null;
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < whereText.size(); i++) {
            if (whereText.get(i).toLowerCase().equals(SqlConstant.AND) || whereText.get(i).toLowerCase().equals("or") ||
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
     * 获取解析后的结构
     * @return List<SelectElement>
     */
    public List<SelectElement> getSelectElementList() {
        return selectElementList;
    }
}
