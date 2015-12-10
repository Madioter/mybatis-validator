package com.madioter.validator.mybatis.model.sql.sqltag.component;

import com.madioter.validator.mybatis.parser.mybatis.component.IComponentNodeParser;
import com.madioter.validator.mybatis.util.MyBatisTagConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
import org.apache.ibatis.builder.xml.dynamic.IfSqlNode;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public class IfSqlComponent implements ISqlComponent {

    /**
     * forEachSqlNode
     */
    private Object ifSqlNode;

    /**
     * test
     */
    private String test;

    /**
     * content
     */
    private ISqlComponent content;

    /**
     * 构造方法
     * @param object IfSqlNode
     */
    public IfSqlComponent(Object object) {
        this.ifSqlNode = object;
        try {
            test = (String) ReflectHelper.getPropertyValue(object, MyBatisTagConstant.TEST);
            Object contents = ReflectHelper.getPropertyValue(object, MyBatisTagConstant.CONTENTS);
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
        return content.toString();
    }
}
