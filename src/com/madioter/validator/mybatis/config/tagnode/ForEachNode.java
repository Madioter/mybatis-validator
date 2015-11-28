package com.madioter.validator.mybatis.config.tagnode;

import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.List;
import org.apache.ibatis.builder.xml.dynamic.ForEachSqlNode;
import org.apache.ibatis.builder.xml.dynamic.MixedSqlNode;
import org.apache.ibatis.builder.xml.dynamic.SqlNode;

/**
 * <Description> <br>
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
    private ForEachSqlNode forEachSqlNode;

    /**
     * 构造方法
     * @param forEachSqlNode ForEachSqlNode
     */
    public ForEachNode(ForEachSqlNode forEachSqlNode) {
        this.forEachSqlNode = forEachSqlNode;
    }

    @Override
    public String toString(){
        try {
            MixedSqlNode mixedSqlNode = (MixedSqlNode) ReflectHelper.getPropertyValue(forEachSqlNode, CONTENTS);
            List<SqlNode> sqlNodeList = (List<SqlNode>) ReflectHelper.getPropertyValue(mixedSqlNode, CONTENTS);
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
