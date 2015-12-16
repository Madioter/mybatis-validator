package com.madioter.validator.mybatis.model.sql.sqlnode;

import com.madioter.validator.mybatis.model.sql.elementnode.FieldNode;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月16日 <br>
 */
public class SetNode {

    /**
     * 字段赋值表达式
     */
    private List<FieldNode> fieldNodeList = new ArrayList<FieldNode>();

    /**
     * Instantiates a new Set node.
     *
     * @param text the text
     */
    public SetNode(List<String> text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.size(); i++) {
            builder.append(text.get(i));
        }

        List<String> fragmentTemps = StringUtil.arrayToList(builder.toString().split(SymbolConstant.SYMBOL_COMMA));
        List<String> fragments = new ArrayList<String>();
        String temp = "";
        for (int i = 0; i < fragmentTemps.size(); i++) {
            if (!StringUtil.isBlank(temp)) {
                temp = temp + SymbolConstant.SYMBOL_COMMA + fragmentTemps.get(i);
            } else {
                temp = fragmentTemps.get(i);
            }
            if (!temp.contains(SymbolConstant.SYMBOL_LEFT_BRACE) || temp.contains(SymbolConstant.SYMBOL_RIGHT_BRACE)) {
                fragments.add(temp);
                temp = "";
            }
        }
        for (int i = 0; i < fragments.size(); i++) {
            String fragment = fragments.get(i);
            fieldNodeList.add(new FieldNode(fragment));
        }

    }

    /**
     * Gets field node list.
     * @return the field node list
     */
    public List<FieldNode> getFieldNodeList() {
        return fieldNodeList;
    }

    /**
     * Sets field node list.
     * @param fieldNodeList the field node list
     */
    public void setFieldNodeList(List<FieldNode> fieldNodeList) {
        this.fieldNodeList = fieldNodeList;
    }
}
