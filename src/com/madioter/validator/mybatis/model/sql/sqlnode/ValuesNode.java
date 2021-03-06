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
                express.lastIndexOf(SymbolConstant.SYMBOL_RIGHT_BRACKET));

        // #{id,jdbcType=Integer},1,now(),#{name,jdbcType=String},#{type}
        List<String> fragmentTemp = StringUtil.arrayToList(express.split(SymbolConstant.SYMBOL_COMMA));
        List<String> fragments = new ArrayList<String>();
        String temp = "";
        for (int i = 0; i < fragmentTemp.size(); i++) {
            if (!StringUtil.isBlank(temp)) {
                temp = temp + SymbolConstant.SYMBOL_COMMA + fragmentTemp.get(i);
            } else {
                temp = fragmentTemp.get(i);
            }
            if (!temp.contains(SymbolConstant.SYMBOL_LEFT_BRACE) || temp.contains(SymbolConstant.SYMBOL_RIGHT_BRACE)) {
                fragments.add(temp);
                temp = "";
            }
        }

        for (int i = 0; i < fragments.size(); i++) {
            if (StringUtil.containBracket(fragments.get(i))) {
                FunctionNode functionNode = new FunctionNode();
                functionNode.setExpress(fragments.get(i));
                selectElementList.add(functionNode);
            } else {
                ValueNode valueNode = new ValueNode();
                valueNode.setValue(fragments.get(i));
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
