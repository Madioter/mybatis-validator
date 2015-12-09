package com.madioter.validator.mybatis.model.sql.sqltag.component;

import com.madioter.validator.mybatis.util.MyBatisTagConstant;
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
public class ForEachSqlComponent implements ISqlComponent{

    /**
     * item
     */
    private static final String ITEM = "item";

    /**
     * open
     */
    private static final String OPEN = "open";

    /**
     * close
     */
    private static final String CLOSE = "close";

    /**
     * forEachSqlNode
     */
    private Object forEachSqlNode;

    /**
     * 构造方法
     * @param forEachSqlNode ForEachSqlNode
     */
    public ForEachSqlComponent(Object forEachSqlNode) {
        this.forEachSqlNode = forEachSqlNode;
    }

    @Override
    public String toString() {
        try {
            Object mixedSqlNode = ReflectHelper.getPropertyValue(forEachSqlNode, MyBatisTagConstant.CONTENTS);
            List<Object> sqlNodeList = (List<Object>) ReflectHelper.getPropertyValue(mixedSqlNode, MyBatisTagConstant.CONTENTS);
            String text = (String) ReflectHelper.getPropertyValue(sqlNodeList.get(0), MyBatisTagConstant.TEXT);
            String item = (String) ReflectHelper.getPropertyValue(forEachSqlNode, ITEM);
            String open = (String) ReflectHelper.getPropertyValue(forEachSqlNode, OPEN);
            String close = (String) ReflectHelper.getPropertyValue(forEachSqlNode, CLOSE);
            text = text.replace(item, ITEM).trim();
            return open + text + close;
        } catch (ConfigException e) {
            e.printStackTrace();
        }
        return "";
    }
}
