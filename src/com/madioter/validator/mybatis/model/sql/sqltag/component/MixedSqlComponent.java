package com.madioter.validator.mybatis.model.sql.sqltag.component;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public class MixedSqlComponent implements ISqlComponent {

    /**
     * forEachSqlNode
     */
    private Object mixedSqlNode;

    /**
     * 构造方法
     * @param mixedSqlNode MixedSqlNode
     */
    public MixedSqlComponent(Object mixedSqlNode) {
        this.mixedSqlNode = mixedSqlNode;
    }

    @Override
    public String toString() {
        return "";
    }
}
