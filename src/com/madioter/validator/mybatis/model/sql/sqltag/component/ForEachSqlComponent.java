package com.madioter.validator.mybatis.model.sql.sqltag.component;

import com.madioter.validator.mybatis.parser.mybatis.component.IComponentNodeParser;
import com.madioter.validator.mybatis.util.MyBatisTagConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.List;


/**
 * <Description> foreache节点 <br>
 * 改用Object为了解决不同版本问题
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月27日 <br>
 */
public class ForEachSqlComponent implements ISqlComponent {

    /**
     * forEachSqlNode
     */
    private Object forEachSqlNode;

    /**
     * collectionExpression
     */
    private String collectionExpression;

    /**
     * content
     */
    private Object content;

    /**
     * open
     */
    private String open;

    /**
     * close
     */
    private String close;

    /**
     * separator
     */
    private String separator;

    /**
     * item
     */
    private String item;

    /**
     * index
     */
    private String index;


    /**
     * 构造方法
     * @param forEachSqlNode ForEachSqlNode
     */
    public ForEachSqlComponent(Object forEachSqlNode) {
        this.forEachSqlNode = forEachSqlNode;
        try {
            collectionExpression = (String) ReflectHelper.getPropertyValue(forEachSqlNode, MyBatisTagConstant.COLLECTION_EXPRESSION);
            separator = (String) ReflectHelper.getPropertyValue(forEachSqlNode, MyBatisTagConstant.SEPARATOR);
            item = (String) ReflectHelper.getPropertyValue(forEachSqlNode, MyBatisTagConstant.ITEM);
            open = (String) ReflectHelper.getPropertyValue(forEachSqlNode, MyBatisTagConstant.OPEN);
            close = (String) ReflectHelper.getPropertyValue(forEachSqlNode, MyBatisTagConstant.CLOSE);
            index = (String) ReflectHelper.getPropertyValue(forEachSqlNode, MyBatisTagConstant.INDEX);
            Object contents = ReflectHelper.getPropertyValue(forEachSqlNode, MyBatisTagConstant.CONTENTS);
            for (IComponentNodeParser componentNodeParser : IComponentNodeParser.SUB_CLASSES) {
                if (componentNodeParser.matches(contents)) {
                    content = componentNodeParser.getComponent(contents);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String contentText = content.toString();
        if (!item.equals(MyBatisTagConstant.ITEM) || !index.equals(MyBatisTagConstant.INDEX)) {
            List<String> vars = StringUtil.extractBrace(contentText);
            for (String var : vars) {
                var.replace(item + SymbolConstant.SYMBOL_POINT, MyBatisTagConstant.ITEM + SymbolConstant.SYMBOL_POINT);
                if (var.equals(index)) {
                    var.replace(index, MyBatisTagConstant.INDEX);
                }
            }
        }
        if (open != null) {
            builder.append(open).append(SymbolConstant.SYMBOL_BLANK);
        }
        builder.append(contentText);
        if (close != null) {
            builder.append(SymbolConstant.SYMBOL_BLANK).append(close);
        }
        return builder.toString();
    }
}
