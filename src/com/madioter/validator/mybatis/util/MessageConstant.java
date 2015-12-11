package com.madioter.validator.mybatis.util;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月04日 <br>
 */
public class MessageConstant {

    /**
     * 异常提示信息：文件名和ID
     */
    public static final String MAPPER_FILE_ID = "文件：%s，ID：%s; ";

    /**
     * 表名
     */
    public static final String TABLE_NAME = " 表名：%s; ";

    /**
     * if标签的test判断条件不一致
     */
    public static final String IF_TEST_TEXT = "COLUMN条件：%s，VALUES条件：%s; ";

    /**
     * 字符表达式
     */
    public static final String EXPRESS_MSG = "表达式为：%s; ";

    /**
     * 表字段名
     */
    public static final String TABLE_COLUMN_NAME = "：表名：%s, 字段名：%s; ";

    /**
     * 当前表
     */
    public static final String CURRENT_TABLE = "@currentTable";
}
