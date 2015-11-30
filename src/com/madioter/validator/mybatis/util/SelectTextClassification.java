package com.madioter.validator.mybatis.util;

/**
 * <Description> select字符串分类标记符<br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月27日 <br>
 */
public enum SelectTextClassification {

    /**
     *  未知，查询字段，表，条件，其他
     */
    NULL,COLUMN,FROM,WHERE,OTHER,LIMIT;

}
