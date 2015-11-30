package com.madioter.validator.mybatis.util;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月30日 <br>
 */
public class SqlUtil {

    /**
     * 验证是否是字段
     * @param express 表达式
     * @return Boolean
     */
    public static boolean checkIsColumn(String express) {
        // TODO 目前只支持 xxx或xxx.xxx的结构，函数比较目前不支持，以后补充
        if (express.matches("^[a-zA-Z]+[\\w+\\.]{0,1}\\w+$")) {
            return true;
        }
        return false;
    }
}
