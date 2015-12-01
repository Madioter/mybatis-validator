package com.madioter.validator.mybatis.config.tagnode;

import com.madioter.validator.mybatis.util.ReflectHelper;
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
public class ForEachNode {

    /**
     * Mybatis解析SqlNode的contents属性名
     */
    private static final String CONTENTS = "contents";

    /**
     * text
     */
    private static final String TEXT = "text";

    /**
     * item
     */
    private static final String ITEM = "item";

    /**
     * forEachSqlNode
     */
    private Object forEachSqlNode;

    /**
     * 构造方法
     * @param forEachSqlNode ForEachSqlNode
     */
    public ForEachNode(Object forEachSqlNode) {
        this.forEachSqlNode = forEachSqlNode;
    }

    @Override
    public String toString() {
        try {
            Object mixedSqlNode = ReflectHelper.getPropertyValue(forEachSqlNode, CONTENTS);
            List<Object> sqlNodeList = (List<Object>) ReflectHelper.getPropertyValue(mixedSqlNode, CONTENTS);
            String text = (String) ReflectHelper.getPropertyValue(sqlNodeList.get(0), TEXT);
            String item = (String) ReflectHelper.getPropertyValue(forEachSqlNode, ITEM);
            String open = (String) ReflectHelper.getPropertyValue(forEachSqlNode, "open");
            String close = (String) ReflectHelper.getPropertyValue(forEachSqlNode, "close");
            text = text.replace(item, ITEM).trim();
            return open + text + close;
        } catch (ConfigException e) {
            e.printStackTrace();
        }
        return "";
    }
}
