package com.madioter.validator.mybatis.model.sql.sqltag.component;

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
     * 构造方法
     * @param object IfSqlNode
     */
    public IfSqlComponent(Object object) {
        this.ifSqlNode = object;
    }

    @Override
    public String toString() {
        return "";
    }
}
