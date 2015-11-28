package com.madioter.validator.mybatis.config.selectnode.constractor;

import com.madioter.validator.mybatis.config.selectnode.FunctionNode;
import com.madioter.validator.mybatis.config.selectnode.QueryNode;
import com.madioter.validator.mybatis.config.selectnode.SelectElement;
import com.madioter.validator.mybatis.util.SymbolConstant;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月28日 <br>
 */
public class ColumnNode {

    /**
     * 最细粒度节点
     */
    private List<SelectElement> selectElementList = new ArrayList<SelectElement>();

    /**
     * 构造字段解析
     * @param columnText 查询条件字段数据
     */
    public ColumnNode(List<String> columnText) {
        SelectElement lastNode = null;
        for (int i = 0; i < columnText.size(); i++) {
            if (columnText.get(i).toLowerCase().equals("distinct") || columnText.get(i).toLowerCase().equals("as")) {
                continue;
            } else {
                String text = columnText.get(i);

                //逗号结尾解析出来只有单项，不包含逗号后的空字符，这里辅助使用了#进行标记，#作为无别称
                if (columnText.get(i).endsWith(SymbolConstant.SYMBOL_COMMA)) {
                    text = text + SymbolConstant.SYMBOL_NUMBER;
                }
                String[] columnTextNode = text.split(SymbolConstant.SYMBOL_COMMA);

                for (int k = 0; k < columnTextNode.length; k++) {
                    if (k > 0) {
                        lastNode = null;
                    }
                    SelectElement currentNode = buildColumnNode(columnTextNode[k], lastNode);
                    if (currentNode != null && lastNode != null && currentNode == lastNode) {
                        continue;
                    } else {
                        lastNode = currentNode;
                        selectElementList.add(currentNode);
                    }
                }
            }
        }
    }

    /**
     * 构造查询语句节点
     *
     * @param text 查询语句字符串
     * @param lastNode 上次节点
     * @return 当前节点
     */
    private SelectElement buildColumnNode(String text, SelectElement lastNode) {
        if (lastNode != null) {
            if (lastNode instanceof QueryNode) {
                ((QueryNode) lastNode).setColumnAlias(text);
                return null;
            } else if (lastNode instanceof FunctionNode) {
                if (((FunctionNode) lastNode).getExpress().contains(")")) {
                    ((FunctionNode) lastNode).setAlias(text);
                    return null;
                } else {
                    FunctionNode functionNode = (FunctionNode) lastNode;
                    functionNode.setExpress(functionNode.getExpress() + SymbolConstant.SYMBOL_BLANK + text);
                }
            }
        } else if (!text.equals(SymbolConstant.SYMBOL_NUMBER)) {
            if (text.contains("(")) {
                FunctionNode node = new FunctionNode();
                node.setExpress(text);
                return node;
            } else {
                QueryNode node = new QueryNode();
                node.setColumnName(text);
                return node;
            }
        }
        return lastNode;
    }

    /**
     * 获取解析后的结构
     * @return List<SelectElement>
     */
    public List<SelectElement> getSelectElementList() {
        return selectElementList;
    }
}
