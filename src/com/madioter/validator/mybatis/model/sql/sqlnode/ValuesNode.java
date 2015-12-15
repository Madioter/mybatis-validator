package com.madioter.validator.mybatis.model.sql.sqlnode;

import com.madioter.validator.mybatis.model.sql.elementnode.FunctionNode;
import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
import com.madioter.validator.mybatis.model.sql.elementnode.ValueNode;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月14日 <br>
 */
public class ValuesNode {

    /**
     * selectElementList
     */
    private List<SelectElement> selectElementList = new ArrayList<SelectElement>();

    /**
     * 构造字段解析
     * @param valueText 查询条件字段数据
     */
    public ValuesNode(List<String> valueText) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < valueText.size(); i++) {
            builder.append(valueText.get(i)).append(SymbolConstant.SYMBOL_BLANK);
        }
        String express = builder.toString();
        express = express.substring(express.indexOf(SymbolConstant.SYMBOL_LEFT_BRACKET) + 1,
                express.lastIndexOf(SymbolConstant.SYMBOL_RIGHT_BRACKET) + 1);

        List<String> fragment = StringUtil.arrayToList(express.split(SymbolConstant.SYMBOL_COMMA));

        for (int i = 0; i < fragment.size(); i++) {
            if (StringUtil.containBracket(fragment.get(i))) {
                FunctionNode functionNode = new FunctionNode();
                functionNode.setExpress(fragment.get(i));
                selectElementList.add(functionNode);
            } else {
                ValueNode valueNode = new ValueNode();
                valueNode.setValue(fragment.get(i));
                selectElementList.add(valueNode);
            }
        }
    }

    /**
     * Gets select element list.
     * @return the select element list
     */
    public List<SelectElement> getSelectElementList() {
        return selectElementList;
    }
}
