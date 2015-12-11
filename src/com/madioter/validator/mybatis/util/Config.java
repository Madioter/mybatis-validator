package com.madioter.validator.mybatis.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月30日 <br>
 */
public class Config {

    /**
     * 不进行验证的入参类型
     */
    public static final Class[] IGNORE_PARAMETER_TYPES = new Class[]{Map.class, Integer.class, int.class, List.class};

    /**
     * 忽略的语句
     */
    public static final String[] IGNORE_STATEMENT_MAPPER_ID = new String[]{
    };
}
