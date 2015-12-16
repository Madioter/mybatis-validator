package com.madioter.validator.mybatis.util;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月16日 <br>
 */
public class SqlHelperConstant {

    /**
     * SELECT_TAG
     */
    public static final String SELECT_TAG = "@select#";

    /**
     * SELECT_TAG_REGEX
     */
    public static final String SELECT_TAG_REGEX = "@select#[0-9]+@";

    /**
     * FRAGMENT_BLANK_TAG
     */
    public static final String FRAGMENT_BLANK_TAG = "@frg#blank@";

    /**
     * FRAGMENT_TAG
     */
    public static final String FRAGMENT_TAG = "@frg#";

    /**
     * JDBC_TYPE_TAG
     */
    public static final String JDBC_TYPE_TAG = ",jdbcType=";

}
