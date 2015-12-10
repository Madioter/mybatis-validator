package com.madioter.validator.mybatis.model.sql.sqltag.component;

import com.madioter.validator.mybatis.util.MyBatisTagConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public class TextSqlComponent implements ISqlComponent {

    /**
     * textSqlNode
     */
    private Object textSqlNode;

    /**
     * text
     */
    private String text;

    /**
     * 构造方法
     * @param textSqlNode TestSqlNode
     */
    public TextSqlComponent(Object textSqlNode) {
        this.textSqlNode = textSqlNode;
        try {
            this.text = (String) ReflectHelper.getPropertyValue(textSqlNode, MyBatisTagConstant.TEXT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        if (text == null) {
            return "";
        }
        return text.trim();
    }
}
